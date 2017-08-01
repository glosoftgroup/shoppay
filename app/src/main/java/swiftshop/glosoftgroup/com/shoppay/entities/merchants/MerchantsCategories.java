package swiftshop.glosoftgroup.com.shoppay.entities.merchants;

/**
 * Created by admin on 1/21/2017.
 */

public class MerchantsCategories {

    private long id;
    private String url;
    private String name;
    private String description;
    private String ImageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String toString() {
        return "merchantsCategories{" +
                "id=" + id +
                ", description=" + description +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
