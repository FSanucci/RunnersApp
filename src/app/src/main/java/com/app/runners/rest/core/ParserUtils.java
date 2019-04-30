package com.app.runners.rest.core;

import android.util.Log;

import com.app.runners.R;
import com.app.runners.model.CoachComment;
import com.app.runners.model.DayPlan;
import com.app.runners.model.Documentation;
import com.app.runners.model.HealthInsurance;
import com.app.runners.model.Link;
import com.app.runners.model.ProfessionalNote;
import com.app.runners.model.Profile;
import com.app.runners.model.Race;
import com.app.runners.model.RunnerComment;
import com.app.runners.model.RunningPlan;
import com.app.runners.model.User;
import com.app.runners.model.WeekPlan;
import com.app.runners.utils.AppController;
import com.facebook.common.internal.ImmutableList;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sergio on 12/2/15.
 */
public class ParserUtils {


    public static final String TAG = "PARSE_UTILS";

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";


    public static String parseToJSON(Object obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T parseFromJSON(String json, Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }

    public static <T> T parseFromJSON(String json, Type cls) {
        return new Gson().fromJson(json, cls);
    }

    // Fixed bug:  "null"  are replaced by null --> http://code.google.com/p/android/issues/detail?id=13830
    public static String optString(JSONObject json, String key) {
        if (json.isNull(key))
            return null;
        else {
            String str = json.optString(key, null);
            if (str != null && str.length() == 0)
                return null;

            return str;
        }
    }

    public static String optString(JSONObject json, String key, String fallback) {
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, fallback);
    }

    public static Date parseDateTime(JSONObject jsonObj, String key) {
        return ParserUtils.parseDate(jsonObj, key, DATE_TIME_FORMAT);
    }

    public static Date parseDate(JSONObject jsonObj, String key, String dateFormat) {
        String dateStr = optString(jsonObj, key);
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    //TODO: Ver con API manejo de fechas
    public static Date parseDefaultDate(JSONObject jsonObj, String key) {
        String dateStr = optString(jsonObj, key);
        if (dateStr != null) {
            try {
                if (dateStr.length() == 10){
                    dateStr = dateStr + "T00:00:00.000Z";
                }

                SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date date = format.parse(dateStr);
                return new Date(date.getTime() + date.getTimezoneOffset() * 60000);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDateTime(String dateStr) {
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                //format.setTimeZone(TimeZone.getDefault());
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDate(String dateStr) {
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static Date parseDate(String dateStr, String dateFormat) {
        if (dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                return format.parse(dateStr);
            } catch (java.text.ParseException e) {
            }
        }
        return null;
    }

    public static String parseDate(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        //format.setTimeZone(TimeZone.getDefault());

        return format.format(date);
    }

    public static String parseDate(Date date) {
        return ParserUtils.parseDate(date, DATE_FORMAT);
    }

    public static String parseDateTime(Date date) {
        return ParserUtils.parseDate(date, DATE_TIME_FORMAT);
    }

    public static String parseTime(Date date) {
        return ParserUtils.parseDate(date, TIME_FORMAT);
    }

    public static String printableDate(Date date) {
        /*
        Date today = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        int month1 = cal1.get(Calendar.MONTH);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int month2 = cal2.get(Calendar.MONTH);

        if(month1 != month2)*/
        return ParserUtils.parseDate(date, "dd 'de' MMMM 'de' yyyy', 'HH:mm' hs'");
        //else return StringHelper.uppercaseFirstCharacter(ParserUtils.parseDate(date,"EEEE dd', 'hh:mm' hs'"));
    }


    public static HVolleyError parseError(JSONObject obj) {
        if (obj == null)
            return new HVolleyError("Error Inesperado", 0);

        String status = ParserUtils.optString(obj, "status");
        if (status == null)
            return new HVolleyError("Error Inesperado", 0);

        try {
            if (!status.equalsIgnoreCase("success")) {
                if (obj.getJSONObject("data") != null && obj.getJSONObject("data").length()>0) {
                    return new HVolleyError(ParserUtils.optString(obj.getJSONObject("data"), "message"), obj.getJSONObject("data").optInt("id", 0), ParserUtils.optString(obj.getJSONObject("data"), "code"));

                } else return new HVolleyError("Error Inesperado", obj.optInt("errorCode", 0));
            }else return null;
        } catch (JSONException e) {
            return new HVolleyError("Error Inesperado", obj.optInt("errorCode", 0));
        }
    }

    public static JSONObject parseResponse(JSONObject obj) {
        if (obj == null)
            return null;

        try {
            return obj.getJSONObject("data");
        } catch (JSONException e) {
            return null;
        }
    }

    public static ArrayList<Race> parseRaces(JSONArray jsonArray) {
        if (jsonArray == null)
            return null;

        ArrayList<Race> list = new ArrayList<Race>();
        for (int i = 0; i < jsonArray.length(); i++) {

            if (jsonArray.opt(i) instanceof String) {
                String obj = jsonArray.optString(i);
                Race race = new Race(obj);
                list.add(race);
            }else{
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String id = ParserUtils.optString(obj1,"_id");
/*
                String body = ParserUtils.optString(obj1,"text");

                String date = ParserUtils.optString(obj1,"date");
                    if (date != null)
                        body +=  " (" + date + ") ";
                String km = ParserUtils.optString(obj1,"distance");
                    if (km != null)
                        body += " Km:" + km;

                String title = ParserUtils.optString(obj1,"author");
                Date creationDate = ParserUtils.parseDate(obj1,"creationDate",DATE_TIME_FORMAT);
                Race race = new Race(id,title,body,creationDate);
*/

                int kmInt = -1;
                int dHourInt = -1;
                int dMinuteInt = -1;
                int dSecondInt = -1;
                String body = ""; //ParserUtils.optString(obj1,"text");
                String date = ParserUtils.optString(obj1,"date");
                if (date != null) {
                    date = applyDateFormat(date);
                    body +=  "Fecha: " + date;
                }
                String km = ParserUtils.optString(obj1,"distance");
                if (km != null){
                    body += "\nKm: " + km;
                    kmInt = Integer.parseInt(km);
                }
                String durationHour = ParserUtils.optString(obj1, "durationHours");
                String durationMinute = ParserUtils.optString(obj1, "durationMinutes");
                String durationSecond = ParserUtils.optString(obj1, "durationSeconds");
/*
                if ((durationHour != null) && (durationMinute != null)){
                    body += "\nDuración: " + durationHour + ":" + durationMinute;
                    dHourInt = Integer.parseInt(durationHour);
                    dMinuteInt = Integer.parseInt(durationMinute);
                }
*/
                if ((durationHour != null) || (durationMinute != null) || (durationSecond != null)){
                    if (durationHour == null){
                        durationHour = "00";
                    } else if (durationHour.length() == 1){
                        durationHour = "0" + durationHour;
                    }

                    if (durationMinute == null){
                        durationMinute = "00";
                    } else if (durationMinute.length() == 1){
                        durationMinute = "0" + durationMinute;
                    }

                    if (durationSecond == null){
                        durationSecond = "00";
                    } else if (durationSecond.length() == 1){
                        durationSecond = "0" + durationSecond;
                    }

                    body += "\nDuración: " + durationHour + ":" + durationMinute + ":" + durationSecond;
                    dHourInt = Integer.parseInt(durationHour);
                    dMinuteInt = Integer.parseInt(durationMinute);
                    dSecondInt = Integer.parseInt(durationSecond);
                }




                String author = ParserUtils.optString(obj1,"author");
                String title = ParserUtils.optString(obj1,"text");
                //Date creationDate = ParserUtils.parseDate(obj1,"creationDate",DATE_TIME_FORMAT);
                //Date runningDate = ParserUtils.parseDate(obj1,"date", DATE_TIME_FORMAT);
                Date creationDate = ParserUtils.parseDefaultDate(obj1,"creationDate");
                Date runningDate = ParserUtils.parseDefaultDate(obj1,"date");

                Race race = new Race(id,title,body,creationDate,runningDate,kmInt, dHourInt, dMinuteInt, dSecondInt, title);
                list.add(race);
            }
        }
        return list;
    }

    private static String applyDateFormat(String dateStr){

        Date date = tryParse(dateStr);
        if (date != null){
            try {
                SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy-MM-dd");
                String newDateStr = simpleDate.format(date);
                if (newDateStr != null){
                    return newDateStr;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateStr;
    }

    private static Date tryParse(String dateString){
        List<String> formatStrings = Arrays.asList("yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        for (String formatString : formatStrings){
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<ProfessionalNote> parseProfessionalNotes(Object obj) {
        if (obj == null)
            return null;

        ArrayList<ProfessionalNote> list = new ArrayList<>();
        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String body = ParserUtils.optString(obj1,"text");
                String title = ParserUtils.optString(obj1,"author");
                Date date = ParserUtils.parseDate(obj1,"creationDate",DATE_TIME_FORMAT);
                ProfessionalNote note = new ProfessionalNote(title,body,date);
                list.add(note);
            }
        }
        return list;
    }

    public static ArrayList<RunnerComment> parseRunnerComments(Object obj) {
        if (obj == null)
            return null;

        ArrayList<RunnerComment> list = new ArrayList<>();
        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String id = ParserUtils.optString(obj1, "_id");
                String title = ParserUtils.optString(obj1,"author");
                String resourceId = ParserUtils.optString(obj1,"resourceId");
                String body = ParserUtils.optString(obj1,"text");
                Date date = ParserUtils.parseDate(obj1,"creationDate",DATE_TIME_FORMAT);
                RunnerComment note = new RunnerComment(id, title,body, resourceId,date);
                list.add(note);
            }
        }
        return list;
    }

    public static ArrayList<CoachComment> parseCoachComments(Object obj) {
        if (obj == null)
            return null;

        ArrayList<CoachComment> list = new ArrayList<>();
        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj1 = jsonArray.optJSONObject(i);
                String id = ParserUtils.optString(obj1, "_id");
                String title = ParserUtils.optString(obj1,"author");
                String resourceId = ParserUtils.optString(obj1,"resourceId");
                String body = ParserUtils.optString(obj1,"text");
                Date date = ParserUtils.parseDate(obj1,"creationDate",DATE_TIME_FORMAT);
                CoachComment note = new CoachComment(id, title,body, resourceId,date);
                list.add(note);
            }
        }
        return list;
    }

    public static WeekPlan parseWeek(JSONObject obj, int weekNumber) {
        WeekPlan plan = new WeekPlan();
        plan.days = new ArrayList<>();
        String [] days= AppController.getInstance().getResources().getStringArray(R.array.days);
        plan.totalKms = "" + obj.optInt("kmTotal",0);
        plan.isVisible = obj.optBoolean("isVisible",true);

        for (int i = 1; i < 8; i++) {
            DayPlan day = new DayPlan();
            day.dayNumber = ""+(i+weekNumber*7);
            day.description = ParserUtils.optString(obj,"day"+i);
            day.kms = ParserUtils.optString(obj,"kmDay"+i);
            day.comments = ParserUtils.optString(obj,"commentRunnerDay"+i);
            day.day = days[i-1];
            plan.days.add(day);
        }

        return plan;
    }

    public static ArrayList<WeekPlan> parseWeeks(JSONObject plan){
        ArrayList<WeekPlan> list = new ArrayList<>();
        int weeks = plan.optInt("totalWeeks", 0);
        for (int i = 1; i < weeks+1; i++) {
            String key = "week"+i;
            WeekPlan week = ParserUtils.parseWeek(plan.optJSONObject(key),i-1);

            if(week!=null)
                list.add(week);
        }
        return list;
    }

    public static Profile parseProfile(JSONObject obj) {
        if (obj == null)
            return null;

        Profile profile = new Profile();
        profile.birthDate = ParserUtils.optString(obj,"birthDate");
        profile.documentNumber = ParserUtils.optString(obj,"documentNumber");
        profile.phoneNumber = ParserUtils.optString(obj,"phoneNumber");
        profile.lastName = ParserUtils.optString(obj,"lastName");
        profile.firstName = ParserUtils.optString(obj,"firstName");
        profile.profilePictureImage = ParserUtils.optString(obj,"profilePictureImage");
        return profile;
    }

    public static HealthInsurance parseHealthInsurance(JSONObject obj) {
        if (obj == null)
            return null;

        HealthInsurance h = new HealthInsurance();
        h.contactTwoPhone = ParserUtils.optString(obj,"contactTwoPhone");
        h.contactTwoName = ParserUtils.optString(obj,"contactTwoName");
        h.contactOnePhone = ParserUtils.optString(obj,"contactOnePhone");
        h.contactOneName = ParserUtils.optString(obj,"contactOneName");
        h.healthInsuranceNumber = ParserUtils.optString(obj,"healthInsuranceNumber");
        h.healthInsuranceName = ParserUtils.optString(obj,"healthInsuranceName");
        return h;
    }

    public static Documentation parseDocumentation(JSONObject obj) {
        if (obj == null)
            return null;

        Documentation h = new Documentation();
        h.swornStatementExpirationDate = ParserUtils.optString(obj,"swornStatementExpirationDate");
        h.medicalCertificateExpirationDate = ParserUtils.optString(obj,"medicalCertificateExpirationDate");
        h.swornStatement = ParserUtils.optString(obj,"swornStatementExpirationImage");
        h.medicalCertificate = ParserUtils.optString(obj,"medicalCertificateImage");
        return h;
    }

    public static ArrayList<Link> parseLinks(JSONArray array){
        if(array==null)
            return null;

        ArrayList<Link> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.optJSONObject(i);
                String title = ParserUtils.optString(obj,"title");
                String url = ParserUtils.optString(obj,"url");
                String desc = ParserUtils.optString(obj,"description");
                String text = ParserUtils.optString(obj,"text");
                Link l = new Link(title,url,text,desc);
                list.add(l);
        }
        return list;
    }

    public static User parseUser(User user, JSONObject dic){
            if (dic != null) {
                try {
                    user.races = ParserUtils.parseRaces(dic.optJSONArray("races"));
                    user.racesWish = ParserUtils.parseRaces(dic.optJSONArray("racesWish"));

                    JSONObject plan = dic.optJSONObject("planActual");
                    if (plan != null) {
                        user.plan = new RunningPlan();
                        user.plan.professionalComments = ParserUtils.parseProfessionalNotes(plan.opt("professionalComments"));
                        user.plan.runnerComments = ParserUtils.parseRunnerComments(plan.opt("runnerComments"));
                        user.plan.coachComments = ParserUtils.parseCoachComments(plan.opt("coachComments"));
                        user.plan.references = ParserUtils.optString(plan, "references");
                        user.plan.microCycle = ParserUtils.optString(plan, "microCycle");
                        user.plan.mesoCycle = ParserUtils.optString(plan, "mesoCycle");
                        user.plan.macroCycle = ParserUtils.optString(plan, "macroCycle");
                        user.plan.stimuli = ParserUtils.optString(plan, "stimuli");

                        user.plan.period = ParserUtils.optString(plan, "timeFrame");
                        user.plan.goals = ParserUtils.optString(plan, "objetive");

                        user.links = ParserUtils.parseLinks(plan.optJSONArray("links"));

                        Date d = ParserUtils.parseDate(plan,"dateFrom","yyyy-MM-dd");
                        if(d!=null) {
                            user.plan.fromDate = ParserUtils.parseDate(d, "dd-MM-yyyy");
                        }

                        d = ParserUtils.parseDate(plan,"dateTo","yyyy-MM-dd");
                        if(d!=null) {
                            user.plan.toDate = ParserUtils.parseDate(d, "dd-MM-yyyy");
                        }

                        user.plan.weekNumberDescription = ParserUtils.optString(plan, "weekNumberDescription");
                        user.plan.name = ParserUtils.optString(plan, "name");
                        user.plan.weeks = ParserUtils.parseWeeks(plan);
                    }

                    user.profile = ParserUtils.parseProfile(dic.optJSONObject("personalData"));
                    user.healthInsurance = ParserUtils.parseHealthInsurance(dic.optJSONObject("healthInsurance"));
                    user.documentation = ParserUtils.parseDocumentation(dic.optJSONObject("documentation"));
                 //   sortArrays(user);
                    return user;

                } catch (Exception e) {
                    return null;
                }
            }
        return user;
    }

    private static void sortArrays(User user){

        if(user.plan == null)
            return;

        if(user.plan.professionalComments != null) {
            Collections.reverse(user.plan.professionalComments);
        }

        if(user.plan.runnerComments != null) {
            Collections.reverse(user.plan.runnerComments);
        }

        if(user.plan.coachComments != null) {
            Collections.reverse(user.plan.coachComments);
        }
    }
}
