package ec.gob.firmadigital.libreria.sign;

import java.io.IOException;

public interface Validator {
   SignValidity validate(byte[] var1) throws IOException;
}
