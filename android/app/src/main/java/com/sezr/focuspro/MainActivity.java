package com.sezr.focuspro;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String PREFS = "sezr_focus_pro_native";
    private static final String BG = "#F6F7FB";
    private static final String TEXT = "#171827";
    private static final String SUB = "#6B7280";
    private static final String BLUE = "#DDEBFF";
    private static final String BLUE2 = "#A7C7FF";
    private static final String YELLOW = "#FFF0B8";
    private static final String YELLOW2 = "#FFE17A";
    private static final String GREEN = "#DDF7EA";
    private static final String GREEN2 = "#A8E6CF";
    private static final String ORANGE = "#FFE2CA";
    private static final String LILAC = "#EEE4FF";
    private static final String RED = "#FFE1E1";

    private int focusDuration = 1500, seconds = 1500, sessions = 0, minutes = 0, target = 60, activeTab = 0;
    private boolean running = false, soundOn = true;
    private String examName = "YKS", examDate = "2026-06-20", profile = "Misafir", musicMode = "Sessiz";
    private final int[] week = new int[7];
    private final List<FocusTask> tasks = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();
    private final AmbientPlayer ambient = new AmbientPlayer();
    private SharedPreferences prefs;
    private LinearLayout content, navBar, taskBox;
    private TextView timer, status, statMin, statSes, targetText, percentText, weeklyText, examText, noteText, profileText, musicText, fullTimer, fullStatus, settingsText;
    private Button toggle, fullToggle;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!running) return;
            if (seconds > 0) { seconds--; updateTimer(); handler.postDelayed(this, 1000); }
            else {
                running = false; ambient.stop();
                int earned = focusDuration / 60;
                sessions++; minutes += earned; week[today()] += earned;
                save(); if (soundOn) beep(); showCompleteDialog(earned); show(activeTab);
            }
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        load(); applyDateReset(); FocusNotificationHelper.ensureChannel(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(24), dp(18), dp(92));
        root.setBackgroundColor(Color.parseColor(BG));
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        navBar = nav();
        root.addView(navBar, m(0, 10, 0, 32));
        setContentView(root);
        show(0);
    }

    private void show(int tab) {
        activeTab = tab;
        refreshNav();
        content.removeAllViews();
        if (tab == 0) home();
        if (tab == 1) focus();
        if (tab == 2) tasks();
        if (tab == 3) exam();
        if (tab == 4) notes();
    }

    private void home() {
        content.addView(screenTitle("Pano", todayText()), m(0,0,0,14));
        content.addView(todayPanel(), m(0,0,0,14));
        content.addView(flowCard(), m(0,0,0,14));
        content.addView(FocusProgressCardFactory.create(this, minutes, sessions), m(0,0,0,14));
        content.addView(weeklyCard(), m(0,0,0,14));
        content.addView(profileCard(), m(0,0,0,14));
        content.addView(settingsCard(), wrap());
        updateAll();
    }

    private void focus() {
        content.addView(screenTitle("Odak", "Sayaç, ses ve tam ekran çalışma"), m(0,0,0,14));
        content.addView(timerCard(), m(0,0,0,14));
        content.addView(musicCard(), wrap());
        updateTimer(); updateMusic();
    }

    private void tasks() {
        content.addView(screenTitle("Görevler", "Bugünkü yapılacakları takip et"), m(0,0,0,14));
        content.addView(tasksCard(), wrap());
        renderTasks();
    }

    private void exam() {
        content.addView(screenTitle("Sınav", "Sınava kalan süreyi gör"), m(0,0,0,14));
        content.addView(examCard(), wrap());
        updateExam();
    }

    private void notes() {
        content.addView(screenTitle("Notlar", "Kısa çalışma notları"), m(0,0,0,14));
        content.addView(notesCard(), wrap());
        renderNotes();
    }

    private LinearLayout screenTitle(String title, String sub) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(2), 0, dp(2), 0);
        TextView s = txt(sub, 15, SUB, false);
        TextView t = txt(title, 34, TEXT, true);
        box.addView(s, wrap());
        box.addView(t, wrap());
        return box;
    }

    private LinearLayout todayPanel() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout top = row();
        LinearLayout texts = new LinearLayout(this); texts.setOrientation(LinearLayout.VERTICAL);
        texts.addView(txt("Bugün ne yapacağım?", 20, TEXT, true), wrap());
        texts.addView(txt("Odak, görev ve sınav durumunu kontrol et", 14, SUB, false), wrap());
        top.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));
        top.addView(pillText("Temiz", GREEN), new LinearLayout.LayoutParams(dp(118), dp(48)));
        c.addView(top, m(0,0,0,14));

        LinearLayout r1 = row();
        r1.addView(summaryBox("☀", "Bugün", minutes + " dk", BLUE), wc());
        r1.addView(summaryBox("✓", "Seans", String.valueOf(sessions), YELLOW), wc());
        r1.addView(summaryBox("🎯", "Hedef", "%" + progress(), RED), wc());
        c.addView(r1, m(0,0,0,10));
        LinearLayout r2 = row();
        r2.addView(summaryWide("Tamamlanan görev", doneTaskCount() + " / " + tasks.size(), GREEN), wc());
        r2.addView(summaryWide("Sınav", daysToExam() + " gün", ORANGE), wc());
        c.addView(r2, wrap());
        return c;
    }

    private LinearLayout flowCard() {
        LinearLayout c = whiteCard();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.addView(txt("Bugünün akışı", 22, TEXT, true), wrap());
        String msg = tasks.isEmpty() ? "Bugün görev yok" : tasks.size() + " görev listede";
        TextView line = txt(msg, 16, SUB, false);
        line.setPadding(0, dp(8), 0, 0);
        c.addView(line, wrap());
        TextView hint = txt("Görev eklenince burada ve Görevler sekmesinde görünür.", 14, SUB, false);
        c.addView(hint, wrap());
        return c;
    }

    private LinearLayout summaryBox(String icon, String label, String value, String color) {
        LinearLayout b = new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL); b.setPadding(dp(14), dp(12), dp(14), dp(12)); b.setBackground(bg(color, 20, "#FFFFFF"));
        b.addView(txt(icon, 22, TEXT, true), wrap());
        b.addView(txt(label, 14, SUB, true), wrap());
        b.addView(txt(value, 21, TEXT, true), wrap());
        return b;
    }

    private LinearLayout summaryWide(String label, String value, String color) {
        LinearLayout b = new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL); b.setPadding(dp(14), dp(12), dp(14), dp(12)); b.setBackground(bg(color, 20, "#FFFFFF"));
        b.addView(txt(label, 14, SUB, true), wrap());
        b.addView(txt(value, 21, TEXT, true), wrap());
        return b;
    }

    private LinearLayout nav() {
        LinearLayout n = new LinearLayout(this);
        n.setOrientation(LinearLayout.HORIZONTAL);
        n.setPadding(dp(8), dp(7), dp(8), dp(7));
        n.setBackground(bg("#F2FFFFFF", 32, "#FFFFFF"));
        n.addView(tab("⌂\nAna",0,YELLOW2), wb());
        n.addView(tab("◷\nOdak",1,BLUE2), wb());
        n.addView(tab("✓\nGörev",2,GREEN2), wb());
        n.addView(tab("□\nSınav",3,ORANGE), wb());
        n.addView(tab("✎\nNot",4,LILAC), wb());
        return n;
    }

    private Button tab(String label, int index, String color) {
        Button b = activeTab == index ? colored(label, color) : navSoft(label);
        b.setTextSize(9);
        b.setAllCaps(false);
        b.setOnClickListener(v -> show(index));
        return b;
    }

    private void refreshNav() {
        if (navBar == null) return;
        navBar.removeAllViews();
        navBar.addView(tab("⌂\nAna",0,YELLOW2), wb());
        navBar.addView(tab("◷\nOdak",1,BLUE2), wb());
        navBar.addView(tab("✓\nGörev",2,GREEN2), wb());
        navBar.addView(tab("□\nSınav",3,ORANGE), wb());
        navBar.addView(tab("✎\nNot",4,LILAC), wb());
    }

    private LinearLayout profileCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Profil",20,TEXT,true),wrap()); profileText=txt("",14,SUB,false); profileText.setPadding(0,dp(8),0,dp(10)); c.addView(profileText,wrap()); EditText input=input("Adını yaz veya misafir kal"); if(!"Misafir".equals(profile)) input.setText(profile); c.addView(input,h(46,0,0,0,8)); LinearLayout r=row(); Button guest=soft("Misafir"), save=colored("Kaydet",YELLOW2); r.addView(guest,wc()); r.addView(save,wc()); c.addView(r,wrap()); guest.setOnClickListener(v->{profile="Misafir";input.setText("");save();updateProfile();}); save.setOnClickListener(v->{String x=input.getText().toString().trim();profile=x.length()==0?"Misafir":x;save();updateProfile();}); return c; }

    private LinearLayout timerCard(){ LinearLayout c=whiteCard(); c.setGravity(Gravity.CENTER); c.setPadding(dp(18),dp(22),dp(18),dp(22)); c.addView(pillText("ODAK SAYACI",YELLOW),center()); timer=txt(format(seconds),68,TEXT,true); timer.setGravity(Gravity.CENTER); timer.setPadding(0,dp(16),0,0); c.addView(timer,wrap()); status=txt(running?"Odak seansı çalışıyor":"Hazır",16,SUB,false); status.setGravity(Gravity.CENTER); status.setPadding(0,dp(8),0,dp(18)); c.addView(status,wrap()); LinearLayout modes=row(); modes.addView(mode("25 dk",25),wc()); modes.addView(mode("45 dk",45),wc()); modes.addView(mode("60 dk",60),wc()); c.addView(modes,m(0,0,0,12)); toggle=colored(running?"Duraklat":"Başlat",GREEN2); c.addView(toggle,height(52)); Button reset=soft("Sıfırla"), full=colored("Tam Ekran Odak",LILAC); c.addView(reset,h(50,0,8,0,0)); c.addView(full,h(50,0,8,0,0)); toggle.setOnClickListener(v->toggleTimer()); reset.setOnClickListener(v->resetTimer()); full.setOnClickListener(v->fullscreen()); return c; }

    private LinearLayout weeklyCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Haftalık Özet",20,TEXT,true),wrap()); weeklyText=txt("",14,SUB,false); weeklyText.setPadding(0,dp(10),0,0); c.addView(weeklyText,wrap()); return c; }
    private LinearLayout settingsCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Ayarlar",20,TEXT,true),wrap()); settingsText=txt("",14,SUB,false); settingsText.setPadding(0,dp(8),0,dp(10)); c.addView(settingsText,wrap()); Button sound=colored(soundOn?"Seans Sesi: Açık":"Seans Sesi: Kapalı",BLUE), reset=colored("Tüm Verileri Sıfırla",RED); c.addView(sound,height(46)); c.addView(reset,h(46,0,8,0,0)); sound.setOnClickListener(v->{soundOn=!soundOn;save();show(0);}); reset.setOnClickListener(v->{clearAll();show(0);}); return c; }
    private LinearLayout musicCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Odak Sesleri",20,TEXT,true),wrap()); musicText=txt("",14,SUB,false); musicText.setPadding(0,dp(8),0,dp(10)); c.addView(musicText,wrap()); LinearLayout a=row(), b=row(); a.addView(music("Sessiz"),wc()); a.addView(music("Yağmur"),wc()); a.addView(music("Kütüphane"),wc()); b.addView(music("Beyaz Gürültü"),wc()); b.addView(music("Lo-fi"),wc()); b.addView(music("Doğa"),wc()); c.addView(a,m(0,0,0,8)); c.addView(b,wrap()); return c; }
    private LinearLayout tasksCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Bugünkü Görevler",20,TEXT,true),wrap()); EditText in=input("Örn. 30 problem çöz"); c.addView(in,h(46,0,12,0,8)); Button add=colored("Görev Ekle",YELLOW2); c.addView(add,height(48)); taskBox=new LinearLayout(this); taskBox.setOrientation(LinearLayout.VERTICAL); taskBox.setPadding(0,dp(12),0,0); c.addView(taskBox,wrap()); Button clear=soft("Tamamlananları Temizle"); c.addView(clear,h(46,0,10,0,0)); add.setOnClickListener(v->{String x=in.getText().toString().trim(); if(x.length()==0)return; tasks.add(new FocusTask(x,false)); in.setText(""); save(); renderTasks();}); clear.setOnClickListener(v->{for(int i=tasks.size()-1;i>=0;i--) if(tasks.get(i).done) tasks.remove(i); save(); renderTasks();}); return c; }
    private LinearLayout examCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Sınav Sayacı",20,TEXT,true),wrap()); c.addView(txt("Tarih formatı: 2026-06-20",12,SUB,false),wrap()); EditText n=input("Sınav adı"); n.setText(examName); c.addView(n,h(46,0,10,0,8)); EditText d=input("yyyy-MM-dd"); d.setText(examDate); c.addView(d,h(46,0,0,0,8)); Button saveBtn=colored("Sınavı Kaydet",YELLOW2); c.addView(saveBtn,height(48)); examText=txt("",16,SUB,false); examText.setPadding(0,dp(12),0,0); c.addView(examText,wrap()); saveBtn.setOnClickListener(v->{examName=n.getText().toString().trim(); if(examName.length()==0) examName="Sınav"; examDate=d.getText().toString().trim(); save(); updateExam();}); return c; }
    private LinearLayout notesCard(){ LinearLayout c=whiteCard(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Notlar",20,TEXT,true),wrap()); EditText in=input("Kısa not ekle"); c.addView(in,h(46,0,12,0,8)); Button add=colored("Not Ekle",YELLOW2), clear=soft("Notları Temizle"); c.addView(add,height(48)); c.addView(clear,h(46,0,8,0,0)); noteText=txt("",14,SUB,false); noteText.setPadding(0,dp(12),0,0); c.addView(noteText,wrap()); add.setOnClickListener(v->{String x=in.getText().toString().trim(); if(x.length()==0)return; notes.add(x); in.setText(""); save(); renderNotes();}); clear.setOnClickListener(v->{notes.clear(); save(); renderNotes();}); return c; }

    private void toggleTimer(){ running=!running; if(running){ ambient.play(musicMode); if(toggle!=null)toggle.setText("Duraklat"); if(status!=null)status.setText("Odak seansı çalışıyor"); handler.post(tick);} else { ambient.stop(); if(toggle!=null)toggle.setText("Devam Et"); if(status!=null)status.setText("Duraklatıldı"); handler.removeCallbacks(tick);} syncFull(); }
    private void resetTimer(){ running=false; ambient.stop(); seconds=focusDuration; handler.removeCallbacks(tick); if(toggle!=null)toggle.setText("Başlat"); if(status!=null)status.setText("Hazır"); updateTimer(); }
    private Button mode(String label,int min){ Button b=soft(label); b.setOnClickListener(v->{ if(running)return; focusDuration=min*60; seconds=focusDuration; if(status!=null)status.setText(label+" modu seçildi"); updateTimer();}); return b; }
    private Button music(String label){ Button b=soft(label); b.setTextSize(9); b.setOnClickListener(v->{musicMode=label; if(running) ambient.play(musicMode); save(); updateMusic();}); return b; }

    private void fullscreen(){ Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE); LinearLayout c=whiteCard(); c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER); c.setPadding(dp(22),dp(22),dp(22),dp(22)); TextView brand=txt("SezR Focus Pro",26,TEXT,true); brand.setGravity(Gravity.CENTER); c.addView(brand,m(0,0,0,28)); fullTimer=txt(format(seconds),84,TEXT,true); fullTimer.setGravity(Gravity.CENTER); c.addView(fullTimer,wrap()); fullStatus=txt(running?"Odak seansı çalışıyor":"Başlamak için hazır",18,SUB,false); fullStatus.setGravity(Gravity.CENTER); fullStatus.setPadding(0,dp(12),0,dp(20)); c.addView(fullStatus,wrap()); TextView ms=txt("Odak sesi: "+musicMode,15,SUB,false); ms.setGravity(Gravity.CENTER); c.addView(ms,m(0,0,0,18)); fullToggle=colored(running?"Duraklat":"Başlat",GREEN2); c.addView(fullToggle,height(56)); Button close=soft("Çık"); c.addView(close,h(56,0,12,0,0)); fullToggle.setOnClickListener(v->toggleTimer()); close.setOnClickListener(v->dlg.dismiss()); dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-1); }
    private void showCompleteDialog(int earned){ Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE); LinearLayout c=whiteCard(); c.setPadding(dp(24),dp(24),dp(24),dp(24)); TextView title=txt("Seans Tamamlandı",24,TEXT,true); title.setGravity(Gravity.CENTER); c.addView(title,wrap()); TextView msg=txt(earned+" dk odak süresi eklendi. Kısa bir mola iyi gelir.",16,SUB,false); msg.setGravity(Gravity.CENTER); msg.setPadding(0,dp(12),0,dp(16)); c.addView(msg,wrap()); Button ok=colored("Tamam",YELLOW2); c.addView(ok,height(52)); ok.setOnClickListener(v->dlg.dismiss()); dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-2); }
    private void beep(){ try{ ToneGenerator tg=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,80); tg.startTone(ToneGenerator.TONE_PROP_ACK,300); }catch(Exception ignored){} }

    private void renderTasks(){ taskBox.removeAllViews(); if(tasks.isEmpty()){ taskBox.addView(txt("Henüz görev eklenmedi.",14,SUB,false),wrap()); return;} for(int i=0;i<tasks.size();i++){ final int ix=i; FocusTask t=tasks.get(i); LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(10),dp(8),dp(10),dp(8)); r.setBackground(bg(t.done?GREEN:"#FFFFFF",16,"#E5E7EF")); TextView label=txt(t.text,15,t.done?"#147A44":TEXT,false); if(t.done) label.setPaintFlags(label.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG); r.addView(label,new LinearLayout.LayoutParams(0,-2,1)); Button done=colored(t.done?"Geri":"Tamam",t.done?LILAC:GREEN), del=colored("Sil",RED); done.setTextSize(10); del.setTextSize(10); r.addView(done,new LinearLayout.LayoutParams(dp(74),dp(40))); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(52),dp(40)); p.setMargins(dp(6),0,0,0); r.addView(del,p); done.setOnClickListener(v->{tasks.get(ix).done=!tasks.get(ix).done; save(); renderTasks();}); del.setOnClickListener(v->{tasks.remove(ix); save(); renderTasks();}); taskBox.addView(r,m(0,0,0,8)); }}
    private void renderNotes(){ if(notes.isEmpty()){noteText.setText("Henüz not eklenmedi.");return;} StringBuilder sb=new StringBuilder(); for(String n:notes) sb.append("• ").append(n).append("\n"); noteText.setText(sb.toString()); }

    private void updateAll(){ if(statMin!=null)statMin.setText(minutes+" dk"); if(statSes!=null)statSes.setText(String.valueOf(sessions)); if(targetText!=null)targetText.setText(minutes+" / "+target+" dk"); if(percentText!=null)percentText.setText("%"+progress()); if(settingsText!=null)settingsText.setText("Varsayılan hedef: "+target+" dk\nSeans sesi: "+(soundOn?"Açık":"Kapalı")); updateWeekly(); updateProfile(); updateMusic(); updateExam(); }
    private void updateTimer(){ if(timer!=null)timer.setText(format(seconds)); syncFull(); }
    private void syncFull(){ if(fullTimer!=null) fullTimer.setText(format(seconds)); if(fullStatus!=null) fullStatus.setText(running?"Odak seansı çalışıyor":"Duraklatıldı"); if(fullToggle!=null) fullToggle.setText(running?"Duraklat":"Devam Et"); }
    private void updateProfile(){ if(profileText!=null) profileText.setText("Merhaba, "+profile+". Bugünkü odak alanın hazır."); }
    private void updateMusic(){ if(musicText!=null) musicText.setText("Seçili odak sesi: "+musicMode+"\nBaşlatınca seçili ses çalar; Sessiz seçilirse ses kapalıdır."); }
    private void updateWeekly(){ if(weeklyText==null)return; String[] n={"Pzt","Sal","Çar","Per","Cum","Cmt","Paz"}; int total=0; StringBuilder sb=new StringBuilder(); for(int i=0;i<7;i++){total+=week[i]; sb.append(n[i]).append(": ").append(week[i]).append(" dk"); if(i<6) sb.append("   ");} sb.append("\nHaftalık toplam: ").append(total).append(" dk"); weeklyText.setText(sb.toString()); }
    private void updateExam(){ if(examText==null)return; try{ Date d=new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(examDate); long diff=d.getTime()-System.currentTimeMillis(); if(diff<=0){examText.setText(examName+" tarihi geçti veya bugün.");return;} examText.setText(examName+" için kalan süre: "+ TimeUnit.MILLISECONDS.toDays(diff)+" gün "+(TimeUnit.MILLISECONDS.toHours(diff)%24)+" saat"); }catch(Exception e){examText.setText("Tarih okunamadı. Örnek: 2026-06-20");} }

    private int progress(){ return target==0?0:Math.min(100,Math.round(minutes*100f/target)); }
    private int doneTaskCount(){ int c=0; for(FocusTask t:tasks) if(t.done)c++; return c; }
    private String daysToExam(){ try{ Date d=new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(examDate); long diff=d.getTime()-System.currentTimeMillis(); return String.valueOf(Math.max(0,TimeUnit.MILLISECONDS.toDays(diff))); }catch(Exception e){ return "?"; } }
    private int today(){ int d= Calendar.getInstance().get(Calendar.DAY_OF_WEEK); return d==Calendar.SUNDAY?6:d-2; }
    private String todayText(){ return new SimpleDateFormat("d MMMM, EEEE", new Locale("tr", "TR")).format(new Date()); }
    private String dayKey(){ return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date()); }
    private String weekKey(){ Calendar c=Calendar.getInstance(); return c.get(Calendar.YEAR)+"-"+c.get(Calendar.WEEK_OF_YEAR); }
    private void applyDateReset(){ String d=dayKey(), w=weekKey(); String oldD=prefs.getString("last_day",d), oldW=prefs.getString("last_week",w); if(!d.equals(oldD)){ minutes=0; sessions=0; tasks.clear(); } if(!w.equals(oldW)){ for(int i=0;i<7;i++)week[i]=0; } prefs.edit().putString("last_day",d).putString("last_week",w).apply(); save(); }
    private void clearAll(){ running=false; ambient.stop(); handler.removeCallbacks(tick); sessions=0; minutes=0; target=60; seconds=focusDuration; profile="Misafir"; musicMode="Sessiz"; soundOn=true; tasks.clear(); notes.clear(); for(int i=0;i<7;i++)week[i]=0; save(); }

    private void load(){ sessions=prefs.getInt("sessions",0); minutes=prefs.getInt("minutes",0); target=prefs.getInt("target",60); soundOn=prefs.getBoolean("sound",true); examName=prefs.getString("exam_name","YKS"); examDate=prefs.getString("exam_date","2026-06-20"); profile=prefs.getString("profile_name","Misafir"); musicMode=prefs.getString("music_mode","Sessiz"); for(int i=0;i<7;i++)week[i]=prefs.getInt("week_"+i,0); String rt=prefs.getString("tasks",""); if(rt!=null) for(String row:rt.split("\\n")) if(row.trim().length()>0) tasks.add(new FocusTask(row.length()>2?row.substring(2):"Görev",row.startsWith("1|"))); String rn=prefs.getString("notes",""); if(rn!=null) for(String row:rn.split("\\n")) if(row.trim().length()>0) notes.add(row.trim()); }
    private void save(){ StringBuilder tb=new StringBuilder(), nb=new StringBuilder(); for(FocusTask t:tasks) tb.append(t.done?"1|":"0|").append(clean(t.text)).append("\n"); for(String n:notes) nb.append(clean(n)).append("\n"); SharedPreferences.Editor e=prefs.edit().putInt("sessions",sessions).putInt("minutes",minutes).putInt("target",target).putBoolean("sound",soundOn).putString("exam_name",examName).putString("exam_date",examDate).putString("profile_name",profile).putString("music_mode",musicMode).putString("tasks",tb.toString()).putString("notes",nb.toString()).putString("last_day",dayKey()).putString("last_week",weekKey()); for(int i=0;i<7;i++)e.putInt("week_"+i,week[i]); e.apply(); }
    private String clean(String v){ return v.replace("\n"," ").replace("|"," "); }
    private String format(int s){ return String.format(Locale.US,"%02d:%02d",s/60,s%60); }

    private LinearLayout whiteCard(){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackground(bg("#FFFFFF",28,"#E5E7EF")); return c; }
    private LinearLayout row(){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); return r; }
    private TextView txt(String v,int sp,String color,boolean bold){ TextView t=new TextView(this); t.setText(v); t.setTextSize(sp); t.setTextColor(Color.parseColor(color)); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private TextView pillText(String v,String color){ TextView t=txt(v,14,TEXT,true); t.setGravity(Gravity.CENTER); t.setPadding(dp(12),dp(8),dp(12),dp(8)); t.setBackground(bg(color,24,"#FFFFFF")); return t; }
    private EditText input(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(Color.parseColor("#8A94A6")); e.setTextColor(Color.parseColor(TEXT)); e.setSingleLine(true); e.setBackground(bg("#FFFFFF",18,"#D9E3F4")); e.setPadding(dp(14),0,dp(14),0); return e; }
    private Button colored(String v,String color){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.parseColor(TEXT)); b.setTextSize(12); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg(color,18,"#FFFFFF")); return b; }
    private Button soft(String v){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.parseColor(TEXT)); b.setTextSize(11); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg("#FFFFFF",18,"#D9E3F4")); return b; }
    private Button navSoft(String v){ Button b=soft(v); b.setBackground(bg("#FFFFFF",20,"#E7EAF3")); return b; }
    private GradientDrawable bg(String fill,int radius,String stroke){ GradientDrawable g=new GradientDrawable(); g.setColor(Color.parseColor(fill)); g.setCornerRadius(dp(radius)); g.setStroke(dp(1),Color.parseColor(stroke)); return g; }
    private LinearLayout.LayoutParams wrap(){ return new LinearLayout.LayoutParams(-1,-2); }
    private LinearLayout.LayoutParams m(int l,int t,int r,int b){ LinearLayout.LayoutParams p=wrap(); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams height(int h){ return new LinearLayout.LayoutParams(-1,dp(h)); }
    private LinearLayout.LayoutParams h(int h,int l,int t,int r,int b){ LinearLayout.LayoutParams p=height(h); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams wb(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1); p.setMargins(dp(3),0,dp(3),0); return p; }
    private LinearLayout.LayoutParams wc(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(dp(4),0,dp(4),0); return p; }
    private LinearLayout.LayoutParams center(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2); p.gravity=Gravity.CENTER_HORIZONTAL; return p; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    @Override protected void onDestroy(){ ambient.stop(); handler.removeCallbacks(tick); save(); super.onDestroy(); }
    private static class FocusTask{ String text; boolean done; FocusTask(String t,boolean d){text=t;done=d;} }
}
