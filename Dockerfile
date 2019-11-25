# Pull base image
# ---------------
FROM store/oracle/weblogic:12.2.1.3-dev


ENV APP_NAME="JDBCApp" \
    APP_PKG_FILE="JDBCApp.war" \
    APP_PKG_LOCATION="/u01/oracle" \
    CONFIG_JVM_ARGS="-Dweblogic.security.SSL.ignoreHostnameVerification=true"

COPY target/JDBCApp.war /u01/oracle/

# Copy files and deploy application in WLST Offline mode
COPY src/main/wls/container-scripts/create-wls-domain.py /u01/oracle/
COPY src/main/wls/container-scripts/ldap-online-config.sh /u01/oracle/
COPY src/main/wls/container-scripts/ldap-config.py /u01/oracle/

#RUN /u01/oracle/oracle_common/common/bin/wlst.sh -loadProperties /u01/oracle/properties/datasource.properties /u01/oracle/ds-deploy.py && \
#    /u01/oracle/oracle_common/common/bin/wlst.sh /u01/oracle/jms-deploy.py  && \
#    /u01/oracle/oracle_common/common/bin/wlst.sh /u01/oracle/app-deploy.py