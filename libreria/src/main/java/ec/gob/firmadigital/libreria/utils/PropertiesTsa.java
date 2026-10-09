package ec.gob.firmadigital.libreria.utils;

public class PropertiesTsa {
   private String tsaUrl = this.readSystemProperty("tsaUrl");
   private String tsaUsername = this.readSystemProperty("tsaUsername");
   private String tsaPassword = this.readSystemProperty("tsaPassword");
   private static final String TSA_URL_SYSTEM_PROPERTY = "tsaUrl";
   private static final String TSA_USERNAME_SYSTEM_PROPERTY = "tsaUsername";
   private static final String TSA_PASSWORD_SYSTEM_PROPERTY = "tsaPassword";

   public String getTsaUrl() {
      return this.tsaUrl;
   }

   public String getTsaUsername() {
      return this.tsaUsername;
   }

   public String getTsaPassword() {
      return this.tsaPassword;
   }

   private String readSystemProperty(String propertyName) {
      String propertyValue = System.getProperty(propertyName);
      if (propertyValue == null) {
         System.out.println("System property " + propertyName + " not found");
         this.tsaUrl = "https://freetsa.org/tsr";
         this.tsaUsername = null;
         this.tsaPassword = null;
      }

      return propertyValue;
   }
}
