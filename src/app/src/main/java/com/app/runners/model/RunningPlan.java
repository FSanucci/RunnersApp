package com.app.runners.model;

import java.util.ArrayList;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class RunningPlan {
    public ArrayList<ProfessionalNote> professionalComments;
    public ArrayList<RunnerComment> runnerComments;
    public ArrayList<CoachComment> coachComments;
    public String references;
    public String microCycle;
    public String mesoCycle;
    public String macroCycle;
    public String stimuli;
    public String weekNumberDescription;
    public String name;
    public ArrayList<WeekPlan> weeks;
    public String fromDate;
    public String toDate;
    public String period;
    public String goals;
}
