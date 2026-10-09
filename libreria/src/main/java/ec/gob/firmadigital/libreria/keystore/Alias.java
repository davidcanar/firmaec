package ec.gob.firmadigital.libreria.keystore;

public class Alias {
   private final String alias;
   private final String name;

   public Alias(String alias, String name) {
      this.alias = alias;
      this.name = name;
   }

   public String getAlias() {
      return this.alias;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.alias == null ? 0 : this.alias.hashCode());
      return 31 * result + (this.name == null ? 0 : this.name.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      Alias other = (Alias)obj;
      if (this.alias == null) {
         if (other.alias != null) {
            return false;
         }
      } else if (!this.alias.equals(other.alias)) {
         return false;
      }

      if (this.name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!this.name.equals(other.name)) {
         return false;
      }

      return true;
   }
}
