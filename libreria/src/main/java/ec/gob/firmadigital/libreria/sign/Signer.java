package ec.gob.firmadigital.libreria.sign;

import ec.gob.firmadigital.libreria.exceptions.InvalidFormatException;
import java.io.IOException;
import java.util.List;

public interface Signer {
   List<SignInfo> getSigners(byte[] var1) throws InvalidFormatException, IOException;
}
