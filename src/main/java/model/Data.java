package model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "data")
public class Data {
    @Id
    @Column(name = "name")
    private String name;
}
