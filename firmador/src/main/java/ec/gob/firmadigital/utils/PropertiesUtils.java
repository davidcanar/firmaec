package ec.gob.firmadigital.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import ec.gob.firmadigital.libreria.exceptions.ConexionException;
import ec.gob.firmadigital.libreria.utils.Json;
import ec.gob.firmadigital.libreria.utils.OsUtils;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesUtils {
   private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class.getName());
   private static final String PATH_PROPERTIES = "/firmadigital.properties";
   private static final String DIRECTORY = System.getProperty("user.home");
   private static final String MESSAGES = "messages.firmador.properties";
   private static final String CONFIG = "config.firmador.properties";
   private static final String CONFIG_RUBRICA = "config.rubrica.properties";
   private static Properties messages;
   private static Properties config;
   private static Properties configRubrica;
   private static Properties firmaDigitalConfig;

   public static Properties getMessages() {
      messages = new Properties();

      try {
         messages.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("messages.firmador.properties"));
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return messages;
   }

   public static Properties getConfig() {
      config = new Properties();

      try {
         config.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("config.firmador.properties"));
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return config;
   }

   public static Properties getConfigRubrica() {
      configRubrica = new Properties();

      try {
         configRubrica.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("config.rubrica.properties"));
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return configRubrica;
   }

   public static Properties getFirmaDigitalConfig() {
      firmaDigitalConfig = new Properties();
      InputStream inputStream = null;

      try {
         inputStream = new FileInputStream(DIRECTORY + "/firmadigital.properties");
         firmaDigitalConfig.load(inputStream);
      } catch (IOException ex) {
         createFirmaDigitalConfig();
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               LOGGER.log(Level.SEVERE, "Error on close file: {0}", e.getMessage());
            }
         }
      }

      return firmaDigitalConfig;
   }

   public static void createFirmaDigitalConfig() {
      Properties properties = new Properties();
      OutputStream outputStream = null;

      try {
         outputStream = new FileOutputStream(DIRECTORY + "/firmadigital.properties");
         properties.store(outputStream, "Temporal configurations");
      } catch (IOException io) {
         LOGGER.log(Level.SEVERE, "Write to file error: +{0}", io.getMessage());
      } finally {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public static void updateFirmaDigitalConfig(String path, String scale) {
      updateFirmaDigitalConfig(path, scale, null);
   }

   public static void updateFirmaDigitalConfig(String path, String scale, String tipoFirmar) {
      if (path == null) {
         path = "";
      }
      if (scale == null) {
         scale = "1.0";
      }
      Properties properties = new Properties();
      properties.setProperty("user.home", path);
      properties.setProperty("uiScale", scale);
      if (tipoFirmar != null) {
         properties.setProperty("tipoFirmar", tipoFirmar);
      } else {
         String tipo = getFirmaDigitalConfig().getProperty("tipoFirmar");
         if (tipo != null) {
            properties.setProperty("tipoFirmar", tipo);
         }
      }
      OutputStream output = null;

      try {
         output = new FileOutputStream(DIRECTORY + "/firmadigital.properties");
         properties.store(output, "Temporal configurations");
      } catch (IOException io) {
         LOGGER.log(Level.SEVERE, "Write to file error: +{0}", io.getMessage());
      } finally {
         if (output != null) {
            try {
               output.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public static String versionBase64() {
      getConfig();
      String version = config.getProperty("version");
      String jsonVersion = Json.generarJsonVersion(OsUtils.getOs(), "FIRMADOR", version);
      return Base64.getEncoder().encodeToString(jsonVersion.getBytes());
   }

   public static boolean getVersion() throws ConexionException {
      getConfigRubrica();
      String restServiceUrl = configRubrica.getProperty("version_url");
      boolean retorno = false;

      try {
         Client client = ClientBuilder.newClient();

         try {
            client.property("jersey.config.client.connectTimeout", configRubrica.getProperty("time_out"));
            client.property("jersey.config.client.readTimeout", configRubrica.getProperty("time_out"));
            WebTarget target = client.target(restServiceUrl);
            Builder builder = target.request();
            Form form = new Form();
            form.param("base64", versionBase64());
            Invocation invocation = builder.buildPost(Entity.form(form));
            Response response = invocation.invoke();
            int statusCode = response.getStatus();
            String respuesta = (String)response.readEntity(String.class);

            String resultado;
            try {
               JsonObject jsonObject = (JsonObject)new Gson().fromJson(respuesta, JsonObject.class);
               resultado = jsonObject.get("resultado").getAsString();
            } catch (NullPointerException | JsonSyntaxException e) {
               throw new ConexionException("Error al decodificar JSON");
            }

            if (resultado.equals("Version enabled")) {
               retorno = true;
            }
         } catch (Throwable var14) {
            if (client != null) {
               try {
                  client.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (client != null) {
            client.close();
         }

         return retorno;
      } catch (ProcessingException e) {
         LOGGER.log(Level.SEVERE, "ProcessingException: ", e.getCause());
         throw new ConexionException("Problemas en la red, no es posible conectarse");
      } catch (BadRequestException e) {
         LOGGER.log(Level.SEVERE, "BadRequestException: ", e.getResponse().readEntity(String.class));
         throw new ConexionException((String)e.getResponse().readEntity(String.class));
      } catch (WebApplicationException e) {
         LOGGER.log(Level.SEVERE, "WebApplicationException: ", e.getResponse().readEntity(String.class));
         throw new ConexionException((String)e.getResponse().readEntity(String.class));
      }
   }
}
