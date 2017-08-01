package swiftshop.glosoftgroup.com.shoppay.entities.merchants;

import java.util.List;

import swiftshop.glosoftgroup.com.shoppay.entities.Metadata;

/**
 * Created by admin on 1/24/2017.
 */

public class MerchantCategoriesResponse {

    private Metadata metadata;
    private List<MerchantsCategories> records;

    public MerchantCategoriesResponse() {
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<MerchantsCategories> getRecords() {
        return records;
    }

    public void setRecords(List<MerchantsCategories> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchantCategoriesResponse)) return false;

        MerchantCategoriesResponse that = (MerchantCategoriesResponse) o;

        if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null) return false;
        return !(getRecords() != null ? !getRecords().equals(that.getRecords()) : that.getRecords() != null);

    }

    @Override
    public int hashCode() {
        int result = getMetadata() != null ? getMetadata().hashCode() : 0;
        result = 31 * result + (getRecords() != null ? getRecords().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MerchantCategoriesResponse{" +
                "metadata=" + metadata +
                ", records=" + records +
                '}';
    }
}
