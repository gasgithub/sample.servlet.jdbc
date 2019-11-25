docker build --no-cache -t jdbc-wls .
docker run -d -p 7001:7001 -p 7002:7002 -p 9002:9002  -v /wlstest:/u01/oracle/properties jdbc-wls

docker build -t nbp-base-wls --build-arg ADMIN_PASSWORD=wlsadmin123 .


docker run -d -p 7001:7001 nbp-base-wls

docker run -d -p 7001:7001 -p 9002:9002  -v /wlstest:/u01/oracle/properties store/oracle/weblogic:12.2.1.3-dev



SSSL host verification:
-Dweblogic.security.SSL.ignoreHostnameVerification=true

Go to the Configuration---Click on SSL tab---Advanced.Select ‘None’ from the Hostname Verification drop down as shown below: