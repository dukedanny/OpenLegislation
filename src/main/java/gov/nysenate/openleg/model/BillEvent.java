package gov.nysenate.openleg.model;



import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Date;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;


@PersistenceCapable
@XmlRootElement
@Cacheable
@XStreamAlias("billevent")
public class BillEvent extends SenateObject implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6454218066307719092L;

	@Persistent 
    @PrimaryKey
	@Column(name="bill_event_id")
    private String billEventId;

	@Persistent
	@Column(name="event_date")
	private Date eventDate;
	
	@Persistent
	@Column(name="event_text")
	private String eventText;
	

	public BillEvent (Bill bill, Date eventDate, String eventText)
	{
		this.eventDate = eventDate; 
		this.eventText = eventText;
		
		try
		{
			this.billEventId = bill.getSenateBillNo() + "-" + eventDate.getTime() + "-" + URLEncoder.encode(eventText,"utf-8");
		}
		catch (Exception e)
		{
			//foo
		}
	}
	
	public String getBillId ()
	{
		return billEventId.substring(0,billEventId.indexOf("-"));
	}
	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}
	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	/**
	 * @return the eventText
	 */
	public String getEventText() {
		return eventText;
	}
	/**
	 * @param eventText the eventText to set
	 */
	public void setEventText(String eventText) {
		this.eventText = eventText;
	}
	
	/**
	 * @return the billEventId
	 */
	public String getBillEventId() {
		return billEventId;
	}

	/**
	 * @param billEventId the billEventId to set
	 */
	public void setBillEventId(String billEventId) {
		this.billEventId = billEventId;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof BillEvent)
		{
			String thisId = getBillEventId();
			String thatId =  ((BillEvent)obj).getBillEventId();
			
			return (thisId.equals(thatId));
		}
		
		return false;
	}
}
	
	

