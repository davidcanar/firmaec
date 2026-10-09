package ec.gob.firmadigital.libreria.certificate.to;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Certificado {
   private String serial;
   private String issuedTo;
   private String issuedBy;
   private Calendar validFrom;
   private Calendar validTo;
   private Calendar revocated;
   private String keyUsages;
   private Boolean certificateValidated;
   private Calendar signGenerated;
   private Boolean signVerify;
   private String docReason;
   private String docLocation;
   private Boolean docValidTimeStamp;
   private String docTimeStampIssuedBy;
   private Date docTimeStamp;
   private String cnTimeStamp;
   private DatosUsuario datosUsuario;

   public Certificado() {
   }

   public Certificado(
      String serial,
      String issuedTo,
      String issuedBy,
      Calendar validFrom,
      Calendar validTo,
      Calendar signGenerated,
      Calendar revocated,
      Boolean certificateValidated,
      DatosUsuario datosUsuario
   ) {
      this.serial = serial;
      this.issuedTo = issuedTo;
      this.issuedBy = issuedBy;
      this.validFrom = validFrom;
      this.validTo = validTo;
      this.signGenerated = signGenerated;
      this.revocated = revocated;
      this.certificateValidated = certificateValidated;
      this.datosUsuario = datosUsuario;
   }

   public String getSerial() {
      return this.serial;
   }

   public void setSerial(String serial) {
      this.serial = serial;
   }

   public String getIssuedTo() {
      return this.issuedTo;
   }

   public void setIssuedTo(String issuedTo) {
      this.issuedTo = issuedTo;
   }

   public String getIssuedBy() {
      return this.issuedBy;
   }

   public void setIssuedBy(String issuedBy) {
      this.issuedBy = issuedBy;
   }

   public Calendar getValidFrom() {
      return this.validFrom;
   }

   public void setValidFrom(Calendar validFrom) {
      this.validFrom = validFrom;
   }

   public Calendar getValidTo() {
      return this.validTo;
   }

   public void setValidTo(Calendar validTo) {
      this.validTo = validTo;
   }

   public Calendar getRevocated() {
      return this.revocated;
   }

   public void setRevocated(Calendar revocated) {
      this.revocated = revocated;
   }

   public String getKeyUsages() {
      return this.keyUsages;
   }

   public void setKeyUsages(String keyUsages) {
      this.keyUsages = keyUsages;
   }

   public Boolean getCertificateValidated() {
      return this.certificateValidated;
   }

   public void setCertificateValidated(Boolean validated) {
      this.certificateValidated = validated;
   }

   public Boolean getSignVerify() {
      return this.signVerify;
   }

   public void setSignVerify(Boolean signVerify) {
      this.signVerify = signVerify;
   }

   public Calendar getSignGenerated() {
      return this.signGenerated;
   }

   public void setSignGenerated(Calendar signGenerated) {
      this.signGenerated = signGenerated;
   }

   public String getDocReason() {
      return this.docReason;
   }

   public void setDocReason(String docReason) {
      this.docReason = docReason;
   }

   public String getDocLocation() {
      return this.docLocation;
   }

   public void setDocLocation(String docLocation) {
      this.docLocation = docLocation;
   }

   public Boolean getDocValidTimeStamp() {
      return this.docValidTimeStamp;
   }

   public void setDocValidTimeStamp(Boolean docValidTimeStamp) {
      this.docValidTimeStamp = docValidTimeStamp;
   }

   public String getDocTimeStampIssuedBy() {
      return this.docTimeStampIssuedBy;
   }

   public void setDocTimeStampIssuedBy(String docTimeStampIssuedBy) {
      this.docTimeStampIssuedBy = docTimeStampIssuedBy;
   }

   public Date getDocTimeStamp() {
      return this.docTimeStamp;
   }

   public void setDocTimeStamp(Date docTimeStamp) {
      this.docTimeStamp = docTimeStamp;
   }

   public String getCnTimeStamp() {
      return this.cnTimeStamp;
   }

   public void setCnTimeStamp(String cnTimeStamp) {
      this.cnTimeStamp = cnTimeStamp;
   }

   public DatosUsuario getDatosUsuario() {
      return this.datosUsuario;
   }

   public void setDatosUsuario(DatosUsuario datosUsuario) {
      this.datosUsuario = datosUsuario;
   }

   @Override
   public String toString() {
      return "\tCertificado\n\t[serial="
         + this.serial
         + "\n\tissuedTo="
         + this.issuedTo
         + "\n\tissuedBy="
         + this.issuedBy
         + "\n\tvalidFrom="
         + (this.validFrom == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.validFrom.getTime()))
         + "\n\tvalidTo="
         + (this.validTo == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.validTo.getTime()))
         + "\n\trevocated="
         + (this.revocated == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.revocated.getTime()))
         + "\n\tkeyUsages="
         + this.keyUsages
         + "\n\tcertificateValidated="
         + this.certificateValidated
         + "\n\tsignGenerated="
         + (this.signGenerated == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.signGenerated.getTime()))
         + "\n\tsignVerify="
         + this.signVerify
         + "\n\tdocReason="
         + this.docReason
         + "\n\tdocLocation="
         + this.docLocation
         + "\n\tdocValidTimeStamp="
         + this.docValidTimeStamp
         + "\n\tdocTimeStampIssuedBy="
         + this.docTimeStampIssuedBy
         + "\n\tdocTimeStamp="
         + (this.docTimeStamp == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.docTimeStamp))
         + "\n\tcnTimeStamp="
         + this.cnTimeStamp
         + "\n\t"
         + (this.datosUsuario == null ? "DatosUsuario[Sin información de usuario]" : this.datosUsuario.toString())
         + "\n\t]\n----------";
   }
}
