package tam3.trabalhos.jorgemartins.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class taskInfo implements Serializable, Comparable<taskInfo> {

    private int id_task;
    private String description;
    private Date deadline;
    private SimpleDateFormat simpleDeadLine = simpleDate();
    private String dateOnString;
    private int lists_id_list; //id da lista que pertence
    private String listname = ""; // nome da lista que pertence
    private String stateontime; //estado do prazo das tarefas, não tem haver com a sua conclusão
    private int concluded = 0; //estar ou não concluida
    private int checkhour = 0; //verificar se a hora foi usada




    public taskInfo () {
        
    }

    public void stringyfyDate() {
        simpleDeadLine = simpleDate();
        dateOnString = simpleDeadLine.format(deadline);
    }



    public boolean isCheckUsedHour() {
        if(checkhour == 1) {
            return true;
        }else {
            return false;
        }
    }

    public void setCheckUsedHour(boolean checkUsedHour) {
        if(checkUsedHour) {
            this.checkhour = 1;
        }else {
            this.checkhour = 0;
        }
        this.simpleDeadLine = simpleDate();
        this.dateOnString = simpleDeadLine.format(deadline);
    }

    public String getDateOnString() {
        return dateOnString;
    }

    public void setDateOnString(String dateOnString) {
        this.dateOnString = dateOnString;
    }

    public String getStateontime() {
        return stateontime;
    }

    public taskInfo(int idTask, int idList, String listName, String description, Date deadline) { //mesmo que o id é da base de dados terei depois de o ir buscar
        this.id_task = idTask;
        this.lists_id_list = idList;
        this.description = description;
        this.deadline = deadline;
        this.dateOnString = simpleDeadLine.format(deadline);
        this.listname = listName;
        verifyDate();
    }

    private SimpleDateFormat simpleDate() {
        String pattern;
        if (checkhour==1) { //para demonstrar ou não demonstrar a hora
            pattern = "yyyy-MM-dd HH:mm";
        } else {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat;

    }


    public String verifyDate() { //verificar se a data já passou em relação à data atual
//todo
        if (concluded==0) {
            if (deadline.before(new Date())) {
                System.out.println(deadline);
                System.out.println(new Date());
                this.stateontime = "Fora de Prazo";
                return this.stateontime;
            } else {
                this.stateontime = "Dentro do Prazo";
                return this.stateontime;
            }
        } else {
            this.stateontime = "Não aplicavel";
            return this.stateontime;
        }
    }

    public void changeConclusion() {
        if (concluded==0) {
            this.concluded = 1;
        } else {
            this.concluded = 0;
        }
    }

    public int getIdTask() {
        return id_task;
    }

    public void setIdTask(int id) {
        this.id_task = id;
    }

    public int getIdList() {
        return lists_id_list;
    }

    public String getListName() {
        return listname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
        this.dateOnString = simpleDeadLine.format(deadline); //para que actualize sempre os dois
    }

    public String isState() {
        return stateontime;
    }

    public void setStateontime(String state) {
        this.stateontime = state;
    }

    public boolean isConcluded() {
        if (concluded==1) {
            return true;
        } else {
            return false;
        }
    }

    public String isConcludedToString() {
        if (concluded==1)
            return "Concluída";
        return "Não Concluída"; //apenas chega aqui se não for porque o outro return não deixa aqui chegar
    }

    public void setConcluded(boolean concluded) {
        if (concluded) {
            this.concluded = 1;
        } else {
            this.concluded = 0;
        }
    }

    @Override
    public String toString() {
        return
                "Descrição='" + description + '\n' +
                        "Deadline=" + deadline + '\n' +
                        "Nome da Lista='" + listname + '\n' +
                        "Estado=" + stateontime + '\n'
                ;
    }


    public void transformObject(taskInfo taskInfo) {
        this.id_task = taskInfo.getIdTask();
        this.lists_id_list = taskInfo.getIdList();
        this.listname = taskInfo.getListName();
        this.description = taskInfo.getDescription();
        this.deadline = taskInfo.getDeadline();
        this.dateOnString = taskInfo.getDateOnString();
        this.stateontime = taskInfo.getStateontime();
        if(taskInfo.isConcluded()) {
            this.concluded = 1;
        } else {
            this.concluded = 0;
        }
        if(taskInfo.isCheckUsedHour()) {
            this.checkhour = 1;
        } else {
            this.checkhour = 0;
        }

    }

    @Override
    public int compareTo(taskInfo taskInfo) { // 2 passo ordenar por data
        if (this.deadline.before(taskInfo.deadline)) {
            return 1;
        }
        if (this.deadline.after(taskInfo.deadline)) {
            return -1;
        }

        return 0;

    }
}
