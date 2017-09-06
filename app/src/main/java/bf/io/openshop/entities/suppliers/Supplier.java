package bf.io.openshop.entities.suppliers;

import com.google.gson.annotations.SerializedName;

public class Supplier {

    private long id;
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    public Supplier() {

    }

    public Supplier(long id, String name,String icon) {
        this.id = id;
        this.name = name;
        this.imageUrl=icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplier Supplier = (Supplier) o;

        if (id != Supplier.id) return false;
        if (name != null ? !name.equals(Supplier.name) : Supplier.name != null) return false;
        return !(imageUrl != null ? !imageUrl.equals(Supplier.imageUrl) : Supplier.imageUrl != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
