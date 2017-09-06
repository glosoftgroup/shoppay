package bf.io.openshop.entities.suppliers;

import java.util.List;

import bf.io.openshop.entities.Metadata;

public class SuppliersResponse {

    private List<Supplier> records;

    public SuppliersResponse() {
    }


    public List<Supplier> getRecords() {
        return records;
    }

    public void setRecords(List<Supplier> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuppliersResponse)) return false;

        SuppliersResponse that = (SuppliersResponse) o;

        //if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null) return false;
        return !(getRecords() != null ? !getRecords().equals(that.getRecords()) : that.getRecords() != null);

    }

    @Override
    public int hashCode() {
       return 0;
    }

    @Override
    public String toString() {
        return "SuppliersResponse{" +
                ", records=" + records +
                '}';
    }
}