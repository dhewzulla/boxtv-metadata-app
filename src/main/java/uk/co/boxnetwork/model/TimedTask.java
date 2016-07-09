package uk.co.boxnetwork.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name="timed_task")
public class TimedTask {
	static final protected Logger logger=LoggerFactory.getLogger(TimedTask.class);
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="task_type")
	private TaskType taskType;
			
	@Column(name="last_time_run")
	private Date lastTimeRun;
	
	@Column(name="run_on_time")
	private String runOnTime;
	
	
	@OneToOne(optional=true, fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn( name = "import_schedule_task_id", nullable = true )
	private ImportScheduleTask importScheduleTask;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public Date getLastTimeRun() {
		return lastTimeRun;
	}

	public void setLastTimeRun(Date lastTimeRun) {
		this.lastTimeRun = lastTimeRun;
	}

	
	
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getRunOnTime() {
		return runOnTime;
	}

	public void setRunOnTime(String runOnTime) {
		this.runOnTime = runOnTime;
	}
    public Date calculateExpectedTime(){    	
    	Calendar calendar=Calendar.getInstance();
    	if(getRunOnTime()!=null){
		    	String timeparts[]=getRunOnTime().split(":");
		    	
		    	for(int i=0;i<timeparts.length;i++){
		    		Integer t=Integer.parseInt(timeparts[i]);
		    		if(i==0){
		    			calendar.set(Calendar.HOUR_OF_DAY,t);    			
		    		}
		    		else if(i==1){
		    			calendar.set(Calendar.MINUTE,t);
		    		}
		    		else if(i==2){
		    			calendar.set(Calendar.SECOND,t);
		    		}    			
		    	}
    	}
    	return calendar.getTime();    	
    }
	
	public ImportScheduleTask getImportScheduleTask() {
		return importScheduleTask;
	}

	public void setImportScheduleTask(ImportScheduleTask importScheduleTask) {
		this.importScheduleTask = importScheduleTask;
	}

	public boolean shouldRun(){
		Date expectedTime=calculateExpectedTime();
		Date now=new Date();
		if((!now.before(expectedTime)) && expectedTime.after(getLastTimeRun())){		
			setLastTimeRun(new Date());
			return true;
		}

		return false;
	}
	
}
