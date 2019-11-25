from java.io import FileInputStream
import java.lang
import os
import string

propInputStream = FileInputStream("/u01/oracle/jms-config.properties")
configProps = Properties()
configProps.load(propInputStream)

# 1 - Connecting details - read from system arguments
##############################
domainname = os.environ.get('DOMAIN_NAME', 'base_domain')
admin_name = os.environ.get('ADMIN_NAME', 'AdminServer')
domainhome = os.environ.get('DOMAIN_HOME', '/u01/oracle/user_projects/domains/' + domainname)
adminport = os.environ.get('ADMIN_PORT', '9002')
username = os.environ.get('ADMIN_USER', 'wlsadmin')
password = os.environ.get('ADMIN_PASSWORD', 'wlsadmin123')

print('admin_name  : [%s]' % admin_name);
print('admin_user  : [%s]' % username);
print('admin_password  : [%s]' % password);
print('admin_port  : [%s]' % adminport);
print('domain_home  : [%s]' % domainhome);

groupBaseDN=configProps.get("")
allUsersFilter=configProps.get("")
userBaseDN=configProps.get("")
ldappass=configProps.get("")
ldapHost=configProps.get("")
allGroupsFilter=configProps.get("")
ldapuser=configProps.get("")

