package ec.gob.firmadigital.libreria.certificate.to;

public class DatosUsuario {
   private String tipoCertificado;
   private String cedula;
   private String nombre;
   private String apellido;
   private String ruc = "";
   private String razonSocial = "";
   private String institucion = "";
   private String cargo = "";
   private String commonName = "";
   private boolean certificadoDigitalValido;
   private String fechaFirmaArchivoP7M;

   public String getTipoCertificado() {
      return this.tipoCertificado;
   }

   public void setTipoCertificado(String tipoCertificado) {
      this.tipoCertificado = tipoCertificado;
   }

   public String getCedula() {
      return this.cedula;
   }

   public void setCedula(String cedula) {
      this.cedula = this.obtenerCedula(cedula);
   }

   private String obtenerCedula(String cedula) {
      return cedula == null ? null : cedula.replace("IDCEC-", "").replace("PASEC-", "");
   }

   public String getNombre() {
      return this.nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public String getApellido() {
      return this.apellido;
   }

   public void setApellido(String apellido) {
      this.apellido = apellido;
   }

   public String getRuc() {
      return this.ruc;
   }

   public void setRuc(String ruc) {
      this.ruc = this.obtenerRuc(ruc);
   }

   private String obtenerRuc(String ruc) {
      return ruc == null ? null : ruc.replace("VATEC-", "");
   }

   public String getRazonSocial() {
      return this.razonSocial;
   }

   public void setRazonSocial(String razonSocial) {
      this.razonSocial = razonSocial;
   }

   public String getInstitucion() {
      return this.institucion;
   }

   public void setInstitucion(String institucion) {
      this.institucion = institucion;
   }

   public String getCargo() {
      return this.cargo;
   }

   public void setCargo(String cargo) {
      this.cargo = cargo;
   }

   public String getCommonName() {
      return this.commonName;
   }

   public void setCommonName(String commonName) {
      this.commonName = commonName;
   }

   public boolean isCertificadoDigitalValido() {
      return this.certificadoDigitalValido;
   }

   public void setCertificadoDigitalValido(boolean certificadoDigitalValido) {
      this.certificadoDigitalValido = certificadoDigitalValido;
   }

   public void setFechaFirmaArchivoP7M(String fechaFirmaArchivoP7M) {
      this.fechaFirmaArchivoP7M = fechaFirmaArchivoP7M;
   }

   @Override
   public String toString() {
      return "DatosUsuario\n\t\t[tipoCertificado="
         + this.tipoCertificado
         + "\n\t\tcedula="
         + this.cedula
         + "\n\t\tnombre="
         + this.nombre
         + "\n\t\tapellido="
         + this.apellido
         + "\n\t\truc="
         + this.ruc
         + "\n\t\trazonSocial="
         + this.razonSocial
         + "\n\t\tinstitucion="
         + this.institucion
         + "\n\t\tcargo="
         + this.cargo
         + "\n\t\tcommonName="
         + this.commonName
         + "\n\t\tcertificadoDigitalValido="
         + this.certificadoDigitalValido
         + "\n\t\tfechaFirmaArchivoP7M="
         + this.fechaFirmaArchivoP7M
         + "\n\t\t]";
   }
}
