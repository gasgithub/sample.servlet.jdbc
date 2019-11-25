#Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
#
#Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
#
# WebLogic on Docker Default Domain
#
# Domain, as defined in DOMAIN_NAME, will be created in this script. Name defaults to 'base_domain'.
#
# Since : October, 2014
# Author: monica.riccelli@oracle.com
# ==============================================
domain_name  = os.environ.get("DOMAIN_NAME", "base_domain")
admin_name  = os.environ.get("ADMIN_NAME", "AdminServer")
admin_listen_port   = int(os.environ.get("ADMIN_LISTEN_PORT", "7001"))
domain_path  = '/u01/oracle/user_projects/domains/%s' % domain_name
production_mode = os.environ.get("PRODUCTION_MODE", "prod")
administration_port_enabled = os.environ.get("ADMINISTRATION_PORT_ENABLED", "true")
administration_port = int(os.environ.get("ADMINISTRATION_PORT", "9002"))

appname    = os.environ.get('APP_NAME', 'auction')
apppkg     = os.environ.get('APP_PKG_FILE', 'auction.war')
appdir     = os.environ.get('APP_PKG_LOCATION', '/u01/oracle')

print('domain_name                 : [%s]' % domain_name);
print('admin_listen_port           : [%s]' % admin_listen_port);
print('domain_path                 : [%s]' % domain_path);
print('production_mode             : [%s]' % production_mode);
print('admin name                  : [%s]' % admin_name);
print('administration_port_enabled : [%s]' % administration_port_enabled);
print('administration_port         : [%s]' % administration_port);

print('appname                     : [%s]' % appname);
print('apppkg                      : [%s]' % apppkg);
print('appdir                      : [%s]' % appdir);

# Open default domain template
# ============================
readTemplate("/u01/oracle/wlserver/common/templates/wls/wls.jar")

set('Name', domain_name)
setOption('DomainName', domain_name)

# Set Administration Port 
# =======================
if administration_port_enabled != "false":
   set('AdministrationPort', administration_port)
   set('AdministrationPortEnabled', 'true')

# Disable Admin Console
# --------------------
# cmo.setConsoleEnabled(false)

# Configure the Administration Server and SSL port.
# =================================================
cd('/Servers/AdminServer')
set('Name', admin_name)
set('ListenAddress', '')
set('ListenPort', admin_listen_port)
if administration_port_enabled != "false":
   create('AdminServer','SSL')
   cd('SSL/AdminServer')
   set('Enabled', 'True')

# Define the user password for weblogic
# =====================================
cd(('/Security/%s/User/weblogic') % domain_name)
cmo.setName(username)
cmo.setPassword(password)

# Write the domain and close the domain template
# ==============================================
setOption('OverwriteDomain', 'true')
setOption('ServerStartMode',production_mode)


# GAS JDBC
# Create Datasource
# ==================
create(dsname, 'JDBCSystemResource')
cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
cmo.setName(dsname)

cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcDataSourceParams','JDBCDataSourceParams')
cd('JDBCDataSourceParams/NO_NAME_0')
set('JNDIName', java.lang.String(dsjndiname))
set('GlobalTransactionsProtocol', java.lang.String('OnePhaseCommit'))

cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcDriverParams','JDBCDriverParams')
cd('JDBCDriverParams/NO_NAME_0')
set('DriverName', dsdriver)
set('URL', dsurl)
set('PasswordEncrypted', dspassword)
set('UseXADataSourceInterface', 'false')

print 'create JDBCDriverParams Properties'
create('myProperties','Properties')
cd('Properties/NO_NAME_0')
create('user','Property')
cd('Property/user')
set('Value', dsusername)

cd('../../')
create('databaseName','Property')
cd('Property/databaseName')
set('Value', dsdbname)

print 'create JDBCConnectionPoolParams'
cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcConnectionPoolParams','JDBCConnectionPoolParams')
cd('JDBCConnectionPoolParams/NO_NAME_0')
set('TestTableName','SQL SELECT 1 FROM DUAL')

# Assign
# ======
assign('JDBCSystemResource', dsname, 'Target', admin_name)

#Create a Persistent Store
#================================================
cd('/')
myfilestore=create('DockerFileStore', 'FileStore')

cd('/FileStores/DockerFileStore')
myfilestore.setDirectory('/u01/oracle/user_projects/domains/base_domain/FileStore')

cd('/')
assign('FileStore', 'DockerFileStore', 'Target', admin_name)


# Create a JMS Server
# ===================
cd('/')
jmsserver=create('DockerJMSServer', 'JMSServer')
print('Create JMSServer : [%s]' % 'DockerJMSServer')

cd('/JMSServers/DockerJMSServer')
set('PersistentStore', 'DockerFileStore')
print('FileStore_name     : [%s]' % getMBean('/FileStores/DockerFileStore'))

cd('/')
assign('JMSServer', 'DockerJMSServer', 'Target', admin_name)

# Create a JMS System resource
# ============================
cd('/')
create('DockerJMSSystemResource', 'JMSSystemResource')
cd('JMSSystemResource/DockerJMSSystemResource/JmsResource/NO_NAME_0')

# Create a JMS Queue and its subdeployment
# ========================================
myq = create('DockerQueue','Queue')
myq.setJNDIName(qjndiname)
myq.setSubDeploymentName('DockerQueueSubDeployment')

cd('/JMSSystemResource/DockerJMSSystemResource')
create('DockerQueueSubDeployment', 'SubDeployment')

# Target resources to the servers
# ===============================
cd('/')
assign('JMSSystemResource.SubDeployment', 'DockerJMSSystemResource.DockerQueueSubDeployment', 'Target', 'DockerJMSServer')


# QCF
# =======
cd('/JMSSystemResources/DockerJMSSystemResource/JmsResource/NO_NAME_0')
connFact = create('DockerConnectionFactory','ConnectionFactory')
connFact.setJNDIName(qcfjndiname)

cd('/JMSSystemResource/DockerJMSSystemResource/JmsResource/NO_NAME_0/ConnectionFactory/DockerConnectionFactory')
set('DefaultTargetingEnabled','true')
create('DockerConnectionFactoryTP', 'TransactionParams')
cd('TransactionParams/NO_NAME_0')
set('XAConnectionFactoryEnabled', 'true')

cd('/')
assign('JMSSystemResource', 'DockerJMSSystemResource', 'Target', admin_name)



# Create Application
# ==================
cd('/')
app = create(appname, 'AppDeployment')
app.setSourcePath(appdir + '/' + apppkg)
app.setStagingMode('nostage')

# Assign application to AdminServer
# =================================
assign('AppDeployment', appname, 'Target', admin_name)





# Create Node Manager
# ===================
#cd('/NMProperties')
#set('ListenAddress','')
#set('ListenPort',5556)
#set('CrashRecoveryEnabled', 'true')
#set('NativeVersionEnabled', 'true')
#set('StartScriptEnabled', 'false')
#set('SecureListener', 'false')
#set('LogLevel', 'FINEST')

# Set the Node Manager user name and password 
# ===========================================
#cd('/SecurityConfiguration/%s' % domain_name)
#set('NodeManagerUsername', username)
#set('NodeManagerPasswordEncrypted', password)

# Write Domain
# ============
writeDomain(domain_path)
closeTemplate()

# Exit WLST
# =========
exit()
