package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.kernel.geom.Rectangle;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RectanguloUtil {
   public static final String POSITION_ON_PAGE_LOWER_LEFT_X = "PositionOnPageLowerLeftX";
   public static final String POSITION_ON_PAGE_LOWER_LEFT_Y = "PositionOnPageLowerLeftY";
   public static final String POSITION_ON_PAGE_UPPER_RIGHT_X = "PositionOnPageUpperRightX";
   public static final String POSITION_ON_PAGE_UPPER_RIGHT_Y = "PositionOnPageUpperRightY";
   private static final Logger LOGGER = Logger.getLogger(RectanguloUtil.class.getName());

   public static Rectangle getPositionOnPage(Properties extraParams) {
      if (extraParams == null) {
         LOGGER.severe("Se ha pedido una posicion para un elemento grafico nulo");
         return null;
      }

      if (extraParams.getProperty("PositionOnPageLowerLeftX") != null
         && extraParams.getProperty("PositionOnPageLowerLeftY") != null
         && extraParams.getProperty("PositionOnPageUpperRightX") != null
         && extraParams.getProperty("PositionOnPageUpperRightY") != null) {
         try {
            return new Rectangle(
               Integer.parseInt(extraParams.getProperty("PositionOnPageLowerLeftX").trim()),
               Integer.parseInt(extraParams.getProperty("PositionOnPageLowerLeftY").trim()),
               Integer.parseInt(extraParams.getProperty("PositionOnPageUpperRightX").trim()),
               Integer.parseInt(extraParams.getProperty("PositionOnPageUpperRightY").trim())
            );
         } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Se ha indicado una posicion invalida para la firma: {0}", e);
         }
      }

      if (extraParams.getProperty("PositionOnPageLowerLeftX") != null
         && extraParams.getProperty("PositionOnPageLowerLeftY") != null
         && extraParams.getProperty("PositionOnPageUpperRightX") == null
         && extraParams.getProperty("PositionOnPageUpperRightY") == null) {
         try {
            return new Rectangle(
               Integer.parseInt(extraParams.getProperty("PositionOnPageLowerLeftX").trim()),
               Integer.parseInt(extraParams.getProperty("PositionOnPageLowerLeftY").trim()) - 36,
               110.0F,
               36.0F
            );
         } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Se ha indicado una posicion invalida para la firma: {0}", e);
         }
      }

      return null;
   }
}
