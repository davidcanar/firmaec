package ec.gob.firmadigital.libreria.keystore;

import ec.gob.firmadigital.libreria.utils.CertificateUtils;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyStoreUtilities {
   private static final Logger LOGGER = Logger.getLogger(KeyStoreUtilities.class.getName());

   public static boolean tieneAliasRepetidos(KeyStore keyStore) {
      try {
         ArrayList<String> aliases = Collections.list(keyStore.aliases());
         HashSet<String> uniqAliases = new HashSet<>(aliases);
         return aliases.size() > uniqAliases.size();
      } catch (KeyStoreException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void fixAliases(KeyStore keyStore) {
      Set<String> tmpAliases = new HashSet<>();

      try {
         Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
         field.setAccessible(true);
         KeyStoreSpi keyStoreVeritable = (KeyStoreSpi)field.get(keyStore);
         if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
            field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
            field.setAccessible(true);

            for (Object entry : (Collection)field.get(keyStoreVeritable)) {
               field = entry.getClass().getDeclaredField("certChain");
               field.setAccessible(true);
               X509Certificate[] certificates = (X509Certificate[])field.get(entry);
               String hashCode = Integer.toString(certificates[0].hashCode());
               field = entry.getClass().getDeclaredField("alias");
               field.setAccessible(true);
               String alias = (String)field.get(entry);
               String tmpAlias = alias;
               int i = 0;

               while (tmpAliases.contains(tmpAlias)) {
                  tmpAlias = alias + "-" + ++i;
               }

               tmpAliases.add(tmpAlias);
               if (!alias.equals(hashCode)) {
                  field.set(entry, tmpAlias);
               }
            }
         }
      } catch (Exception e) {
         LOGGER.severe(e.getMessage());
      }
   }

   public static List<Alias> getSigningAliases(KeyStore keyStore) {
      try {
         Enumeration<String> aliases = keyStore.aliases();
         List<Alias> aliasList = new ArrayList<>();

         while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate certificate = (X509Certificate)keyStore.getCertificate(alias);

            try {
               certificate.checkValidity();
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
               LOGGER.log(Level.WARNING, "Certificado expirado: {0}", certificate.getIssuerX500Principal().toString());
            }

            String name = CertificateUtils.getCN(certificate);
            boolean[] keyUsage = certificate.getKeyUsage();
            if (keyUsage != null && keyUsage[0]) {
               aliasList.add(new Alias(alias, name));
            }
         }

         return aliasList;
      } catch (KeyStoreException e) {
         throw new IllegalStateException(e);
      }
   }
}
