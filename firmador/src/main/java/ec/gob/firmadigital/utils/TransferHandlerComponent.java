package ec.gob.firmadigital.utils;

import ec.gob.firmadigital.firmador.JPanelConfiguracion;
import ec.gob.firmadigital.firmador.Main;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class TransferHandlerComponent extends TransferHandler {
   private final List<String> listExtension;
   private final Main jFramePrincipal;
   private final JPanelConfiguracion jPanelConfiguracion;

   public TransferHandlerComponent(Main jFramePrincipal, List<String> listExtension) {
      this.listExtension = listExtension;
      this.jFramePrincipal = jFramePrincipal;
      this.jPanelConfiguracion = null;
   }

   public TransferHandlerComponent(JPanelConfiguracion jPanelConfiguracion, List<String> listExtension) {
      this.listExtension = listExtension;
      this.jFramePrincipal = null;
      this.jPanelConfiguracion = jPanelConfiguracion;
   }

   @Override
   public boolean canImport(JComponent com, DataFlavor[] dataFlavors) {
      for (DataFlavor flavor : dataFlavors) {
         if (flavor.equals(DataFlavor.javaFileListFlavor)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean importData(JComponent jComponent, Transferable transferable) {
      try {
         List<String> rutaDocumentos = new ArrayList<>();

         for (File file : (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor)) {
            for (String str : this.listExtension) {
               if (this.getFileExtension(file.getName()).toLowerCase().equals(str)) {
                  if (jComponent.getName().equals("jbtnCertificado")) {
                     this.jPanelConfiguracion.setPathCertificado(file.getCanonicalPath());
                     this.jPanelConfiguracion.jbtnCertificado();
                  }

                  if (jComponent.getName().equals("jbtnCertificadoFirmar")) {
                     this.jFramePrincipal.setPathCertificadoFirmar(file.getCanonicalPath());
                     this.jFramePrincipal.jbtnCertificadoFirmar();
                  }

                  if (jComponent.getName().equals("jbtnCertificadoValidar")) {
                     this.jFramePrincipal.setPathCertificadoValidar(file.getCanonicalPath());
                     this.jFramePrincipal.jbtnCertificadoValidar();
                  }

                  if ((
                        jComponent.getName().equals("jbtnDocumentoFirmar")
                           || jComponent.getName().equals("jScrollPaneFirmarDocumentos")
                           || jComponent.getName().equals("jbtnDocumentoVerificar")
                           || jComponent.getName().equals("jScrollPaneVerificarDocumentos")
                     )
                     && this.getFileExtension(file.getName()).toLowerCase().equals(str)) {
                     rutaDocumentos.add(file.getCanonicalPath());
                  }
               }
            }
         }

         if (jComponent.getName().equals("jbtnDocumentoFirmar") || jComponent.getName().equals("jScrollPaneFirmarDocumentos")) {
            this.jFramePrincipal.agregarFirmarDocumento(rutaDocumentos);
         }

         if (jComponent.getName().equals("jbtnDocumentoVerificar") || jComponent.getName().equals("jScrollPaneVerificarDocumentos")) {
            this.jFramePrincipal.agregarVerificarDocumento(rutaDocumentos);
         }

         return true;
      } catch (IOException ioe) {
         System.err.println("IOError getting data: " + ioe);
      } catch (UnsupportedFlavorException ufe) {
         System.err.println("Unsupported Flavor: " + ufe);
      }

      return false;
   }

   private String getFileExtension(String fileName) {
      try {
         return fileName.substring(fileName.lastIndexOf(".") + 1);
      } catch (Exception e) {
         return null;
      }
   }
}
