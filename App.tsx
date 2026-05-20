import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import { COLORS } from '../../theme/colors';
export default function StatsScreen(){return <GradientBackground><ScrollView contentContainerStyle={styles.container}><Text style={styles.title}>Ritim</Text><View style={styles.grid}><GlassCard style={styles.stat}><Text style={styles.value}>60 dk</Text><Text style={styles.label}>Ortalama</Text></GlassCard><GlassCard style={styles.stat}><Text style={styles.value}>3 gün</Text><Text style={styles.label}>Seri</Text></GlassCard></View><GlassCard><Text style={styles.section}>Haftalık yorum</Text><Text style={styles.text}>Bu hafta ritim oluşmaya başladı. Kısa seanslarla devam et.</Text></GlassCard></ScrollView></GradientBackground>}
const styles=StyleSheet.create({container:{padding:18,paddingBottom:110},title:{color:COLORS.text,fontSize:32,fontWeight:'900',marginTop:18,marginBottom:14},grid:{flexDirection:'row',gap:12,marginBottom:16},stat:{flex:1,alignItems:'center'},value:{color:COLORS.primary,fontSize:26,fontWeight:'900'},label:{color:COLORS.muted,fontWeight:'800',marginTop:4},section:{color:COLORS.primary,fontWeight:'900',fontSize:18,marginBottom:8},text:{color:COLORS.text,fontWeight:'800',lineHeight:22}});
