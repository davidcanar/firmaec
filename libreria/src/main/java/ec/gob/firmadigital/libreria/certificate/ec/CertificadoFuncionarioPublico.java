package ec.gob.firmadigital.libreria.certificate.ec;

public interface CertificadoFuncionarioPublico extends CertificadoPersonaNatural {
   String getInstitucion();

   String getCargo();

   String getRazonSocial();
}
