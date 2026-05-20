import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import { COLORS } from '../../theme/colors';
import { useFocusStore } from '../../store/focusStore';

export default function HomeScreen(){
  const tasks=useFocusStore(s=>s.tasks); const totalToday=useFocusStore(s=>s.totalToday);
  const done=tasks.filter(t=>t.done).length; const rate=tasks.length?Math.round(done/tasks.length*100):0;
  return <GradientBackground><ScrollView contentContainerStyle={styles.container}>
    <View style={styles.header}><View style={styles.logo}><Text style={styles.logoText}>S<Text style={styles.logoGold}>R</Text></Text></View><View><Text style={styles.title}>SezR Focus</Text><Text style={styles.subtitle}>Görev • Odak • Sınav Koçu</Text></View></View>
    <GlassCard style={styles.hero}><Text style={styles.badge}>Öğrenci Koçu</Text><Text style={styles.heroTitle}>Bugünkü çalışma ritmini birlikte başlatalım.</Text><Text style={styles.heroText}>İlk kritik görevi seç, 25 dakikalık seansla başla ve günü ölçülebilir kapat.</Text><NeonButton title="Odak seansına başla"/></GlassCard>
    <View style={styles.stats}><GlassCard style={styles.stat}><Text style={styles.statValue}>{Math.floor(totalToday/60)} dk</Text><Text style={styles.statLabel}>Bugün</Text></GlassCard><GlassCard style={styles.stat}><Text style={styles.statValue}>%{rate}</Text><Text style={styles.statLabel}>Görev</Text></GlassCard><GlassCard style={styles.stat}><Text style={styles.statValue}>82</Text><Text style={styles.statLabel}>Skor</Text></GlassCard></View>
    <GlassCard><Text style={styles.section}>Akıllı Öneri</Text><Text style={styles.text}>{tasks.length===done?'Bugünkü görevler tamam. Kısa tekrar yeterli.':'Kritik görevi ilk seansa al. Diğerleri bekleyebilir.'}</Text></GlassCard>
  </ScrollView></GradientBackground>
}
const styles=StyleSheet.create({
  container:{padding:18,paddingBottom:110},header:{flexDirection:'row',alignItems:'center',gap:12,marginTop:18,marginBottom:18},
  logo:{width:54,height:54,borderRadius:20,backgroundColor:'rgba(250,204,21,.16)',alignItems:'center',justifyContent:'center',borderWidth:1,borderColor:'rgba(250,204,21,.28)'},logoText:{color:COLORS.text,fontWeight:'900',fontSize:24},logoGold:{color:COLORS.primary},
  title:{color:COLORS.text,fontSize:25,fontWeight:'900'},subtitle:{color:COLORS.muted,fontWeight:'800',marginTop:3},hero:{marginBottom:14},badge:{color:COLORS.primary,fontWeight:'900',marginBottom:12},
  heroTitle:{color:COLORS.text,fontSize:30,lineHeight:36,fontWeight:'900',letterSpacing:-1},heroText:{color:COLORS.muted,fontWeight:'800',lineHeight:22,marginVertical:14},
  stats:{flexDirection:'row',gap:10,marginBottom:14},stat:{flex:1,alignItems:'center',padding:14},statValue:{color:COLORS.primary,fontSize:22,fontWeight:'900'},statLabel:{color:COLORS.muted,fontWeight:'800',fontSize:12,marginTop:4},
  section:{color:COLORS.primary,fontWeight:'900',fontSize:18,marginBottom:8},text:{color:COLORS.text,fontWeight:'800',lineHeight:22}
});
