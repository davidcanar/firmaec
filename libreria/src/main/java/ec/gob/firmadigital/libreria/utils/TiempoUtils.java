package ec.gob.firmadigital.libreria.utils;

import ec.gob.firmadigital.libreria.exceptions.HoraServidorException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TiempoUtils {
   private static final Logger LOGGER = Logger.getLogger(TiempoUtils.class.getName());
   private static final int DEFAULT_TIME_OUT = 5000;
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

   private static Properties getConfigRubrica() {
      Properties configRubrica = new Properties();

      try {
         configRubrica.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("config.rubrica.properties"));
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return configRubrica;
   }

   public static Date getFechaHora(String apiUrl, String base64) throws HoraServidorException {
      String fechaHora = null;

      try {
         fechaHora = getFechaHoraServidor(apiUrl, base64);
         TemporalAccessor accessor = DATE_TIME_FORMATTER.parse(fechaHora);
         return Date.from(Instant.from(accessor));
      } catch (IOException | NullPointerException | HoraServidorException e) {
         LOGGER.log(Level.WARNING, "No se pudo obtener la fecha del servidor, se usara la hora local: {0}", e.getMessage());
         return new Date();
      } catch (DateTimeParseException e) {
         LOGGER.log(Level.WARNING, "La fecha indicada (''{0}'') no sigue el patron ISO-8601, se usara la hora local: {1}", new Object[]{fechaHora, e.getMessage()});
         return new Date();
      }
   }

   public static String getFechaHoraServidor(String apiUrl, String base64) throws IOException, HoraServidorException {
      Properties configRubrica = getConfigRubrica();
      String timeOutProperty = configRubrica.getProperty("time_out");
      int timeOut = timeOutProperty != null ? Integer.parseInt(timeOutProperty) : 5000;
      String fecha_hora_url = apiUrl == null ? configRubrica.getProperty("fecha_hora_url") : apiUrl;
      if (fecha_hora_url == null) {
         return null;
      }

      Client client = ClientBuilder.newClient();

      try {
         client.property("jersey.config.client.connectTimeout", timeOut);
         client.property("jersey.config.client.readTimeout", timeOut);
         WebTarget target = client.target(fecha_hora_url);
         Builder builder = target.request(new String[]{"text/plain"});
         Form form = new Form();
         form.param("base64", base64);
         Invocation invocation = builder.buildPost(Entity.form(form));
         Response response = invocation.invoke();
         int statusCode = response.getStatus();
         String respuesta = (String)response.readEntity(String.class);
         if (statusCode != 200) {
            throw new HoraServidorException(PropertiesUtils.getMessages().getProperty("mensaje.error.problema_red"));
         }

         return respuesta;
      } catch (ProcessingException | HoraServidorException e) {
         LOGGER.log(Level.WARNING, "No se pudo obtener la hora del servidor, se usara la hora local: {0}", e.getMessage());
         return OffsetDateTime.now().format(DATE_TIME_FORMATTER);
      } finally {
         try {
            client.close();
         } catch (Exception e) {
            LOGGER.log(Level.FINE, "Error cerrando el cliente: {0}", e.getMessage());
         }
      }
   }
}
