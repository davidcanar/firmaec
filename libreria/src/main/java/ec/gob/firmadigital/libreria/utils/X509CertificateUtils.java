package ec.gob.firmadigital.libreria.utils;

import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.exceptions.ConexionException;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.HoraServidorException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class X509CertificateUtils {
   private String error = null;
   private String revocado = null;
   private boolean expirado = false;
   private boolean desconocido = false;

   public boolean isExpirado() {
      return this.expirado;
   }

   public boolean isDesconocido() {
      return this.desconocido;
   }

   public String getRevocado() {
      return this.revocado;
   }

   public String getError() {
      return this.error;
   }

   public static String getCedulaRuc(KeyStore keyStore, String alias) throws EntidadCertificadoraNoValidaException {
      try {
         X509Certificate certificate = (X509Certificate)keyStore.getCertificate(alias);
         DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(certificate);
         return datosUsuario.getCedula() != null ? datosUsuario.getCedula() : datosUsuario.getRuc();
      } catch (KeyStoreException e) {
         throw new RuntimeException(e);
      }
   }

   public boolean validarX509Certificate(X509Certificate x509Certificate, String apiUrl, String base64) throws RubricaException, KeyStoreException, EntidadCertificadoraNoValidaException, InvalidKeyException, CertificadoInvalidoException, IOException, HoraServidorException, ConexionException {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      boolean retorno = false;
      int diasAnticipacion = 0;
      if (x509Certificate != null) {
         String apiUrlFecha = null;
         String apiUrlRevocado = null;
         if (apiUrl != null) {
            apiUrlFecha = apiUrl + "/fecha-hora";
            apiUrlRevocado = apiUrl + "/certificado/fechaRevocado";
         }

         Date fechaHora = TiempoUtils.getFechaHora(apiUrlFecha, base64);
         Date fechaRevocado = UtilsCrlOcsp.validarFechaRevocado(x509Certificate, apiUrlRevocado);
         if (fechaRevocado != null && fechaRevocado.compareTo(fechaHora) <= 0) {
            this.revocado = simpleDateFormat.format(fechaRevocado.getTime());
         }

         if (fechaHora.compareTo(x509Certificate.getNotBefore()) > 0 && fechaHora.compareTo(x509Certificate.getNotAfter()) < 0) {
            Calendar calendarRecordatorio = Calendar.getInstance(TimeZone.getTimeZone("America/Guayaquil"));
            calendarRecordatorio.setTime(x509Certificate.getNotAfter());
            calendarRecordatorio.add(5, -diasAnticipacion);
            if (calendarRecordatorio.getTime().compareTo(fechaHora) <= 0) {
               this.error = PropertiesUtils.getMessages().getProperty("mensaje.advertencia.certificado_advertencia")
                  + simpleDateFormat.format(x509Certificate.getNotAfter().getTime());
            }
         } else {
            this.expirado = true;
         }

         if (!Utils.verifySignature(x509Certificate)) {
            this.desconocido = true;
         }

         if (this.revocado == null && !this.expirado && !this.desconocido) {
            retorno = true;
         } else {
            this.error = PropertiesUtils.getMessages().getProperty("mensaje.error.certificado_invalido");
            retorno = false;
         }
      }

      return retorno;
   }
}
