import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="test")
public class TextMessage {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idtest;
	
	@Column(name = "text")
	private String text;
	
	public String getText(){
		return text;
	}
}
