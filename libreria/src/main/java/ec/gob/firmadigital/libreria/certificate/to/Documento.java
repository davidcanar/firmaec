package ec.gob.firmadigital.libreria.certificate.to;

import java.util.List;

public class Documento {
   private Boolean signValidate;
   private Boolean docValidate;
   private List<Certificado> certificados;
   private String error;

   public Documento() {
   }

   public Documento(Boolean signValidate, Boolean docValidate, List<Certificado> certificados, String error) {
      this.signValidate = signValidate;
      this.docValidate = docValidate;
      this.certificados = certificados;
      this.error = error;
   }

   public Boolean getSignValidate() {
      return this.signValidate;
   }

   public void setSignValidate(Boolean signValidate) {
      this.signValidate = signValidate;
   }

   public Boolean getDocValidate() {
      return this.docValidate;
   }

   public void setDocValidate(Boolean docValidate) {
      this.docValidate = docValidate;
   }

   public List<Certificado> getCertificados() {
      return this.certificados;
   }

   public void setCertificados(List<Certificado> certificados) {
      this.certificados = certificados;
   }

   public String getError() {
      return this.error;
   }

   public void setError(String error) {
      this.error = error;
   }

   @Override
   public String toString() {
      return "Documento\n\t[signValidate=" + this.signValidate + "\n\tdocValidate=" + this.docValidate + "\n\terror=" + this.error + "\n\t]\n----------";
   }
}
