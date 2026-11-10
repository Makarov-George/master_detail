package masterdetail.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "detail")
public class Detail {
    protected Detail(){}

    public Detail(String name, BigDecimal totalDet){
        this.name = name;
        this.totalDet = totalDet;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "total_det")
    private BigDecimal totalDet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Master document;

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public BigDecimal getTotalDet(){
        return totalDet;
    }
    public void setTotalDet(BigDecimal totalDet){
        this.totalDet = totalDet;
    }

    public Master getDocument(){
        return document;
    }
    public void setDocument(Master document){
        this.document = document;
    }
}
