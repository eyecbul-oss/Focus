package com.sezr.focuspro;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.ToneGenerator;
import android.media.AudioManager;
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
    private int focusDuration = 1500, seconds = 1500, sessions = 0, minutes = 0, target = 60, activeTab = 0;
    private boolean running = false, soundOn = true;
    private String examName = "YKS", examDate = "2026-06-20", profile = "Misafir", musicMode = "Sessiz";
    private final int[] week = new int[7];
    private final List<FocusTask> tasks = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();
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
                running = false;
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
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(12), dp(12), dp(12), dp(10)); root.setBackgroundColor(Color.parseColor("#020617"));
        root.addView(header(), m(0,0,0,8));
        ScrollView scroll = new ScrollView(this); content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        navBar = nav(); root.addView(navBar, m(0,8,0,0));
        setContentView(root); show(0);
    }

    private LinearLayout header() { LinearLayout c = card(); c.setGravity(Gravity.CENTER); c.setPadding(dp(12), dp(10), dp(12), dp(10)); TextView t = txt("SezR Focus Pro", 24, "#facc15", true); t.setGravity(Gravity.CENTER); c.addView(t, wrap()); TextView s = txt("Bağımsız Android odak uygulaması", 12, "#cbd5e1", false); s.setGravity(Gravity.CENTER); c.addView(s, wrap()); return c; }

    private LinearLayout nav() { LinearLayout n = row(); n.addView(tab("Ana",0), wb()); n.addView(tab("Odak",1), wb()); n.addView(tab("Görev",2), wb()); n.addView(tab("Sınav",3), wb()); n.addView(tab("Not",4), wb()); return n; }
    private Button tab(String label, int index) { Button b = index == activeTab ? primary(label) : dark(label); b.setTextSize(10); b.setOnClickListener(v -> show(index)); return b; }
    private void refreshNav() { if (navBar == null) return; navBar.removeAllViews(); navBar.addView(tab("Ana",0), wb()); navBar.addView(tab("Odak",1), wb()); navBar.addView(tab("Görev",2), wb()); navBar.addView(tab("Sınav",3), wb()); navBar.addView(tab("Not",4), wb()); }

    private void show(int tab) { activeTab = tab; refreshNav(); content.removeAllViews(); if (tab==0) home(); if (tab==1) focus(); if (tab==2) tasks(); if (tab==3) exam(); if (tab==4) notes(); }
    private void home() { content.addView(profileCard(), m(0,0,0,10)); content.addView(statsRow(), m(0,0,0,10)); content.addView(FocusProgressCardFactory.create(this, minutes, sessions), m(0,0,0,10)); content.addView(targetCard(), m(0,0,0,10)); content.addView(weeklyCard(), m(0,0,0,10)); content.addView(settingsCard(), wrap()); updateAll(); }
    private void focus() { content.addView(timerCard(), m(0,0,0,10)); content.addView(musicCard(), wrap()); updateTimer(); updateMusic(); }
    private void tasks() { content.addView(tasksCard(), wrap()); renderTasks(); }
    private void exam() { content.addView(examCard(), wrap()); updateExam(); }
    private void notes() { content.addView(notesCard(), wrap()); renderNotes(); }

    private LinearLayout profileCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Profil / Giriş",20,"#facc15",true),wrap()); profileText=txt("",15,"#dbeafe",false); profileText.setPadding(0,dp(8),0,dp(10)); c.addView(profileText,wrap()); EditText input=input("Adını yaz veya misafir kal"); if(!"Misafir".equals(profile)) input.setText(profile); c.addView(input,h(48,0,0,0,8)); LinearLayout r=row(); Button guest=dark("Misafir"), save=primary("Kaydet"); r.addView(guest,wb()); r.addView(save,wb()); c.addView(r,wrap()); guest.setOnClickListener(v->{profile="Misafir";input.setText("");save();updateProfile();}); save.setOnClickListener(v->{String x=input.getText().toString().trim();profile=x.length()==0?"Misafir":x;save();updateProfile();}); return c; }

    private LinearLayout timerCard(){ LinearLayout c=card(); c.setGravity(Gravity.CENTER); c.setPadding(dp(16),dp(20),dp(16),dp(20)); c.addView(pill("ODAK SAYACI"),center()); timer=txt(format(seconds),62,"#ffffff",true); timer.setGravity(Gravity.CENTER); timer.setPadding(0,dp(16),0,0); c.addView(timer,wrap()); status=txt(running?"Odak seansı çalışıyor":"Hazır",15,"#cbd5e1",false); status.setGravity(Gravity.CENTER); status.setPadding(0,dp(8),0,dp(16)); c.addView(status,wrap()); LinearLayout modes=row(); modes.addView(mode("25 dk",25),wb()); modes.addView(mode("45 dk",45),wb()); modes.addView(mode("60 dk",60),wb()); c.addView(modes,m(0,0,0,12)); toggle=primary(running?"Duraklat":"Başlat"); c.addView(toggle,height(52)); Button reset=dark("Sıfırla"), full=dark("Tam Ekran Odak Modu"); c.addView(reset,h(50,0,8,0,0)); c.addView(full,h(50,0,8,0,0)); toggle.setOnClickListener(v->toggleTimer()); reset.setOnClickListener(v->resetTimer()); full.setOnClickListener(v->fullscreen()); return c; }

    private LinearLayout statsRow(){ LinearLayout r=row(); statMin=stat(minutes+" dk","Bugünkü çalışma"); statSes=stat(String.valueOf(sessions),"Tamamlanan seans"); r.addView((LinearLayout)statMin.getParent(),wc()); r.addView((LinearLayout)statSes.getParent(),wc()); return r; }
    private LinearLayout targetCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Günlük Hedef",20,"#facc15",true),wrap()); targetText=txt("",15,"#dbeafe",false); targetText.setPadding(0,dp(8),0,dp(8)); c.addView(targetText,wrap()); percentText=txt("",30,"#ffffff",true); c.addView(percentText,wrap()); LinearLayout r=row(); r.setPadding(0,dp(10),0,0); r.addView(targetBtn("30 dk",30),wb()); r.addView(targetBtn("60 dk",60),wb()); r.addView(targetBtn("90 dk",90),wb()); c.addView(r,wrap()); return c; }
    private LinearLayout weeklyCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Haftalık Özet",20,"#facc15",true),wrap()); weeklyText=txt("",14,"#dbeafe",false); weeklyText.setPadding(0,dp(10),0,0); c.addView(weeklyText,wrap()); return c; }

    private LinearLayout settingsCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Ayarlar",20,"#facc15",true),wrap()); settingsText=txt("",14,"#dbeafe",false); settingsText.setPadding(0,dp(8),0,dp(10)); c.addView(settingsText,wrap()); Button sound=dark(soundOn?"Ses: Açık":"Ses: Kapalı"), reset=dark("Tüm Verileri Sıfırla"); c.addView(sound,height(48)); c.addView(reset,h(48,0,8,0,0)); sound.setOnClickListener(v->{soundOn=!soundOn;save();show(0);}); reset.setOnClickListener(v->{clearAll();show(0);}); return c; }

    private LinearLayout musicCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Müzik / Odak Atmosferi",20,"#facc15",true),wrap()); musicText=txt("",14,"#dbeafe",false); musicText.setPadding(0,dp(8),0,dp(10)); c.addView(musicText,wrap()); LinearLayout a=row(), b=row(); a.addView(music("Sessiz"),wb()); a.addView(music("Yağmur"),wb()); a.addView(music("Kütüphane"),wb()); b.addView(music("Beyaz Gürültü"),wb()); b.addView(music("Lo-fi"),wb()); b.addView(music("Doğa"),wb()); c.addView(a,m(0,0,0,8)); c.addView(b,wrap()); return c; }
    private LinearLayout tasksCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Bugünkü görevler",20,"#facc15",true),wrap()); EditText in=input("Örn. 30 problem çöz"); c.addView(in,h(48,0,12,0,8)); Button add=primary("Görev Ekle"); c.addView(add,height(50)); taskBox=new LinearLayout(this); taskBox.setOrientation(LinearLayout.VERTICAL); taskBox.setPadding(0,dp(12),0,0); c.addView(taskBox,wrap()); Button clear=dark("Tamamlananları Temizle"); c.addView(clear,h(48,0,10,0,0)); add.setOnClickListener(v->{String x=in.getText().toString().trim(); if(x.length()==0)return; tasks.add(new FocusTask(x,false)); in.setText(""); save(); renderTasks();}); clear.setOnClickListener(v->{for(int i=tasks.size()-1;i>=0;i--) if(tasks.get(i).done) tasks.remove(i); save(); renderTasks();}); return c; }
    private LinearLayout examCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Sınav Sayacı",20,"#facc15",true),wrap()); c.addView(txt("Tarih formatı: 2026-06-20",12,"#94a3b8",false),wrap()); EditText n=input("Sınav adı"); n.setText(examName); c.addView(n,h(48,0,10,0,8)); EditText d=input("yyyy-MM-dd"); d.setText(examDate); c.addView(d,h(48,0,0,0,8)); Button saveBtn=primary("Sınavı Kaydet"); c.addView(saveBtn,height(50)); examText=txt("",16,"#dbeafe",false); examText.setPadding(0,dp(12),0,0); c.addView(examText,wrap()); saveBtn.setOnClickListener(v->{examName=n.getText().toString().trim(); if(examName.length()==0) examName="Sınav"; examDate=d.getText().toString().trim(); save(); updateExam();}); return c; }
    private LinearLayout notesCard(){ LinearLayout c=card(); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.addView(txt("Notlar",20,"#facc15",true),wrap()); EditText in=input("Kısa not ekle"); c.addView(in,h(48,0,12,0,8)); Button add=primary("Not Ekle"), clear=dark("Notları Temizle"); c.addView(add,height(50)); c.addView(clear,h(48,0,8,0,0)); noteText=txt("",14,"#cbd5e1",false); noteText.setPadding(0,dp(12),0,0); c.addView(noteText,wrap()); add.setOnClickListener(v->{String x=in.getText().toString().trim(); if(x.length()==0)return; notes.add(x); in.setText(""); save(); renderNotes();}); clear.setOnClickListener(v->{notes.clear(); save(); renderNotes();}); return c; }

    private void toggleTimer(){ running=!running; if(running){ if(toggle!=null)toggle.setText("Duraklat"); if(status!=null)status.setText("Odak seansı çalışıyor"); handler.post(tick);} else { if(toggle!=null)toggle.setText("Devam Et"); if(status!=null)status.setText("Duraklatıldı"); handler.removeCallbacks(tick);} syncFull(); }
    private void resetTimer(){ running=false; seconds=focusDuration; handler.removeCallbacks(tick); if(toggle!=null)toggle.setText("Başlat"); if(status!=null)status.setText("Hazır"); updateTimer(); }
    private Button mode(String label,int min){ Button b=dark(label); b.setOnClickListener(v->{ if(running)return; focusDuration=min*60; seconds=focusDuration; if(status!=null)status.setText(label+" modu seçildi"); updateTimer();}); return b; }
    private Button targetBtn(String label,int min){ Button b=dark(label); b.setOnClickListener(v->{target=min; save(); show(0);}); return b; }
    private Button music(String label){ Button b=dark(label); b.setTextSize(9); b.setOnClickListener(v->{musicMode=label; save(); updateMusic();}); return b; }

    private void fullscreen(){ Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE); LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER); c.setPadding(dp(22),dp(22),dp(22),dp(22)); c.setBackgroundColor(Color.parseColor("#020617")); TextView brand=txt("SezR Focus Pro",26,"#facc15",true); brand.setGravity(Gravity.CENTER); c.addView(brand,m(0,0,0,28)); fullTimer=txt(format(seconds),84,"#ffffff",true); fullTimer.setGravity(Gravity.CENTER); c.addView(fullTimer,wrap()); fullStatus=txt(running?"Odak seansı çalışıyor":"Başlamak için hazır",18,"#cbd5e1",false); fullStatus.setGravity(Gravity.CENTER); fullStatus.setPadding(0,dp(12),0,dp(20)); c.addView(fullStatus,wrap()); TextView ms=txt("Atmosfer: "+musicMode,15,"#94a3b8",false); ms.setGravity(Gravity.CENTER); c.addView(ms,m(0,0,0,18)); fullToggle=primary(running?"Duraklat":"Başlat"); c.addView(fullToggle,height(56)); Button close=dark("Çık"); c.addView(close,h(56,0,12,0,0)); fullToggle.setOnClickListener(v->toggleTimer()); close.setOnClickListener(v->dlg.dismiss()); dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-1); }
    private void showCompleteDialog(int earned){ Dialog dlg=new Dialog(this); dlg.requestWindowFeature(Window.FEATURE_NO_TITLE); LinearLayout c=card(); c.setPadding(dp(24),dp(24),dp(24),dp(24)); TextView title=txt("Seans Tamamlandı",24,"#facc15",true); title.setGravity(Gravity.CENTER); c.addView(title,wrap()); TextView msg=txt(earned+" dk odak süresi eklendi. Kısa bir mola iyi gelir.",16,"#dbeafe",false); msg.setGravity(Gravity.CENTER); msg.setPadding(0,dp(12),0,dp(16)); c.addView(msg,wrap()); Button ok=primary("Tamam"); c.addView(ok,height(52)); ok.setOnClickListener(v->dlg.dismiss()); dlg.setContentView(c); dlg.show(); Window w=dlg.getWindow(); if(w!=null) w.setLayout(-1,-2); }
    private void beep(){ try{ ToneGenerator tg=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,80); tg.startTone(ToneGenerator.TONE_PROP_ACK,300); }catch(Exception ignored){} }

    private void renderTasks(){ taskBox.removeAllViews(); if(tasks.isEmpty()){ taskBox.addView(txt("Henüz görev eklenmedi.",14,"#cbd5e1",false),wrap()); return;} for(int i=0;i<tasks.size();i++){ final int ix=i; FocusTask t=tasks.get(i); LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(10),dp(8),dp(10),dp(8)); r.setBackground(bg(t.done?"#10251a":"#0f172a",14,t.done?"#1f6f3a":"#263244")); TextView label=txt(t.text,15,t.done?"#86efac":"#e5e7eb",false); if(t.done) label.setPaintFlags(label.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG); r.addView(label,new LinearLayout.LayoutParams(0,-2,1)); Button done=dark(t.done?"Geri":"Tamam"), del=dark("Sil"); done.setTextSize(11); del.setTextSize(11); r.addView(done,new LinearLayout.LayoutParams(dp(76),dp(40))); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(54),dp(40)); p.setMargins(dp(6),0,0,0); r.addView(del,p); done.setOnClickListener(v->{tasks.get(ix).done=!tasks.get(ix).done; save(); renderTasks();}); del.setOnClickListener(v->{tasks.remove(ix); save(); renderTasks();}); taskBox.addView(r,m(0,0,0,8)); }}
    private void renderNotes(){ if(notes.isEmpty()){noteText.setText("Henüz not eklenmedi.");return;} StringBuilder sb=new StringBuilder(); for(String n:notes) sb.append("• ").append(n).append("\n"); noteText.setText(sb.toString()); }

    private void updateAll(){ if(statMin!=null)statMin.setText(minutes+" dk"); if(statSes!=null)statSes.setText(String.valueOf(sessions)); if(targetText!=null)targetText.setText(minutes+" / "+target+" dk"); if(percentText!=null)percentText.setText("%"+(target==0?0:Math.min(100,Math.round(minutes*100f/target)))); if(settingsText!=null)settingsText.setText("Varsayılan hedef: "+target+" dk\nSes: "+(soundOn?"Açık":"Kapalı")); updateWeekly(); updateProfile(); updateMusic(); updateExam(); }
    private void updateTimer(){ if(timer!=null)timer.setText(format(seconds)); syncFull(); }
    private void syncFull(){ if(fullTimer!=null) fullTimer.setText(format(seconds)); if(fullStatus!=null) fullStatus.setText(running?"Odak seansı çalışıyor":"Duraklatıldı"); if(fullToggle!=null) fullToggle.setText(running?"Duraklat":"Devam Et"); }
    private void updateProfile(){ if(profileText!=null) profileText.setText("Merhaba, "+profile+". Bugünkü odak alanın hazır."); }
    private void updateMusic(){ if(musicText!=null) musicText.setText("Seçili atmosfer: "+musicMode+"\nŞimdilik seçim kaydı ve seans bitiş sesi aktif."); }
    private void updateWeekly(){ if(weeklyText==null)return; String[] n={"Pzt","Sal","Çar","Per","Cum","Cmt","Paz"}; int total=0; StringBuilder sb=new StringBuilder(); for(int i=0;i<7;i++){total+=week[i]; sb.append(n[i]).append(": ").append(week[i]).append(" dk"); if(i<6) sb.append("\n");} sb.append("\n\nHaftalık toplam: ").append(total).append(" dk"); weeklyText.setText(sb.toString()); }
    private void updateExam(){ if(examText==null)return; try{ Date d=new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(examDate); long diff=d.getTime()-System.currentTimeMillis(); if(diff<=0){examText.setText(examName+" tarihi geçti veya bugün.");return;} examText.setText(examName+" için kalan süre: "+ TimeUnit.MILLISECONDS.toDays(diff)+" gün "+(TimeUnit.MILLISECONDS.toHours(diff)%24)+" saat"); }catch(Exception e){examText.setText("Tarih okunamadı. Örnek: 2026-06-20");} }
    private int today(){ int d= Calendar.getInstance().get(Calendar.DAY_OF_WEEK); return d==Calendar.SUNDAY?6:d-2; }
    private String dayKey(){ return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date()); }
    private String weekKey(){ Calendar c=Calendar.getInstance(); return c.get(Calendar.YEAR)+"-"+c.get(Calendar.WEEK_OF_YEAR); }
    private void applyDateReset(){ String d=dayKey(), w=weekKey(); String oldD=prefs.getString("last_day",d), oldW=prefs.getString("last_week",w); if(!d.equals(oldD)){ minutes=0; sessions=0; tasks.clear(); } if(!w.equals(oldW)){ for(int i=0;i<7;i++)week[i]=0; } prefs.edit().putString("last_day",d).putString("last_week",w).apply(); save(); }
    private void clearAll(){ running=false; handler.removeCallbacks(tick); sessions=0; minutes=0; target=60; seconds=focusDuration; profile="Misafir"; musicMode="Sessiz"; soundOn=true; tasks.clear(); notes.clear(); for(int i=0;i<7;i++)week[i]=0; save(); }

    private void load(){ sessions=prefs.getInt("sessions",0); minutes=prefs.getInt("minutes",0); target=prefs.getInt("target",60); soundOn=prefs.getBoolean("sound",true); examName=prefs.getString("exam_name","YKS"); examDate=prefs.getString("exam_date","2026-06-20"); profile=prefs.getString("profile_name","Misafir"); musicMode=prefs.getString("music_mode","Sessiz"); for(int i=0;i<7;i++)week[i]=prefs.getInt("week_"+i,0); String rt=prefs.getString("tasks",""); if(rt!=null) for(String row:rt.split("\\n")) if(row.trim().length()>0) tasks.add(new FocusTask(row.length()>2?row.substring(2):"Görev",row.startsWith("1|"))); String rn=prefs.getString("notes",""); if(rn!=null) for(String row:rn.split("\\n")) if(row.trim().length()>0) notes.add(row.trim()); }
    private void save(){ StringBuilder tb=new StringBuilder(), nb=new StringBuilder(); for(FocusTask t:tasks) tb.append(t.done?"1|":"0|").append(clean(t.text)).append("\n"); for(String n:notes) nb.append(clean(n)).append("\n"); SharedPreferences.Editor e=prefs.edit().putInt("sessions",sessions).putInt("minutes",minutes).putInt("target",target).putBoolean("sound",soundOn).putString("exam_name",examName).putString("exam_date",examDate).putString("profile_name",profile).putString("music_mode",musicMode).putString("tasks",tb.toString()).putString("notes",nb.toString()).putString("last_day",dayKey()).putString("last_week",weekKey()); for(int i=0;i<7;i++)e.putInt("week_"+i,week[i]); e.apply(); }
    private String clean(String v){ return v.replace("\n"," ").replace("|"," "); }
    private String format(int s){ return String.format(Locale.US,"%02d:%02d",s/60,s%60); }

    private LinearLayout card(){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackground(bg("#111827",22,"#243041")); return c; }
    private LinearLayout row(){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); return r; }
    private TextView txt(String v,int sp,String color,boolean bold){ TextView t=new TextView(this); t.setText(v); t.setTextSize(sp); t.setTextColor(Color.parseColor(color)); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private EditText input(String hint){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(Color.parseColor("#64748b")); e.setTextColor(Color.WHITE); e.setSingleLine(true); e.setBackground(bg("#0f172a",14,"#263244")); e.setPadding(dp(14),0,dp(14),0); return e; }
    private TextView pill(String v){ TextView t=txt(v,12,"#facc15",true); t.setGravity(Gravity.CENTER); t.setPadding(dp(14),dp(8),dp(14),dp(8)); t.setBackground(bg("#241a08",22,"#5f4712")); return t; }
    private Button primary(String v){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.parseColor("#111827")); b.setTextSize(14); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg("#facc15",16,"#facc15")); return b; }
    private Button dark(String v){ Button b=new Button(this); b.setText(v); b.setTextColor(Color.WHITE); b.setTextSize(13); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setBackground(bg("#1e293b",16,"#334155")); return b; }
    private TextView stat(String value,String label){ LinearLayout box=card(); box.setPadding(dp(14),dp(14),dp(14),dp(14)); TextView n=txt(value,23,"#facc15",true); box.addView(n,wrap()); box.addView(txt(label,12,"#cbd5e1",false),wrap()); return n; }
    private GradientDrawable bg(String fill,int radius,String stroke){ GradientDrawable g=new GradientDrawable(); g.setColor(Color.parseColor(fill)); g.setCornerRadius(dp(radius)); g.setStroke(dp(1),Color.parseColor(stroke)); return g; }
    private LinearLayout.LayoutParams wrap(){ return new LinearLayout.LayoutParams(-1,-2); }
    private LinearLayout.LayoutParams m(int l,int t,int r,int b){ LinearLayout.LayoutParams p=wrap(); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams height(int h){ return new LinearLayout.LayoutParams(-1,dp(h)); }
    private LinearLayout.LayoutParams h(int h,int l,int t,int r,int b){ LinearLayout.LayoutParams p=height(h); p.setMargins(dp(l),dp(t),dp(r),dp(b)); return p; }
    private LinearLayout.LayoutParams wb(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(44),1); p.setMargins(dp(2),0,dp(2),0); return p; }
    private LinearLayout.LayoutParams wc(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(dp(4),0,dp(4),0); return p; }
    private LinearLayout.LayoutParams center(){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2); p.gravity=Gravity.CENTER_HORIZONTAL; return p; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    @Override protected void onDestroy(){ handler.removeCallbacks(tick); save(); super.onDestroy(); }
    private static class FocusTask{ String text; boolean done; FocusTask(String t,boolean d){text=t;done=d;} }
}
