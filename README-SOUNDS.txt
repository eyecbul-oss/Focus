import React,{useState} from 'react';
import { ScrollView, StyleSheet, Text, TextInput } from 'react-native';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import TaskCard from '../../components/tasks/TaskCard';
import { COLORS } from '../../theme/colors';
import { useFocusStore } from '../../store/focusStore';

export default function TasksScreen(){
  const [title,setTitle]=useState(''); const tasks=useFocusStore(s=>s.tasks); const addTask=useFocusStore(s=>s.addTask); const toggleTask=useFocusStore(s=>s.toggleTask);
  return <GradientBackground><ScrollView contentContainerStyle={styles.container}>
    <Text style={styles.title}>Görevler</Text>
    <GlassCard style={styles.inputCard}><TextInput placeholder="Örn. Matematik: Problemler 20 soru" placeholderTextColor={COLORS.muted} value={title} onChangeText={setTitle} style={styles.input}/><NeonButton title="Görev Ekle" onPress={()=>{if(title.trim()){addTask(title.trim(),'Soru','normal');setTitle('')}}}/></GlassCard>
    <Text style={styles.section}>Bugünkü Görevler</Text>{tasks.map(t=><TaskCard key={t.id} task={t} onPress={()=>toggleTask(t.id)}/>)}
  </ScrollView></GradientBackground>
}
const styles=StyleSheet.create({container:{padding:18,paddingBottom:110},title:{color:COLORS.text,fontSize:32,fontWeight:'900',marginTop:18,marginBottom:14},inputCard:{gap:12,marginBottom:18},input:{minHeight:52,borderRadius:18,backgroundColor:'rgba(2,6,23,.48)',color:COLORS.text,paddingHorizontal:14,fontWeight:'800',borderWidth:1,borderColor:COLORS.border},section:{color:COLORS.primary,fontSize:18,fontWeight:'900',marginBottom:12}});
