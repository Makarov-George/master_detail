package masterdetail.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table(name = "master")
public class Master {
    protected Master() {}

    public Master(Integer number, LocalDate date, String note){
        this.number = number;
        this.date = date;
        this.note = note;
        this.total = BigDecimal.ZERO;
    }

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "document")
    private List<Detail> details = new ArrayList<>();

    public Long getId(){
        return id;
    }

    public Integer getNumber(){
        return number;
    }
    public void setNumber(Integer number){
        this.number = number;
    }

    public LocalDate getDate(){
        return date;
    }
    public void setDate(LocalDate date){
        this.date = date;
    }

    public BigDecimal getTotal(){
        return total;
    }

    public String getNote(){
        return note; 
    }
    public void setNote(String note){
        this.note = note;
    }

    public List<Detail> getDetails(){
        return details;
    }

    public void addDetail(Detail detail){
        details.add(detail);
        detail.setDocument(this);
        this.recalculateTotal();
    }

    public void recalculateTotal(){
        BigDecimal sum = BigDecimal.ZERO;
        for (Detail detail: details){
            sum = sum.add(detail.getTotalDet());
        }
        this.total = sum;
    }
}
