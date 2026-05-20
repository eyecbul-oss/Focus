import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { FocusTask } from '../../store/focusStore';
import { COLORS } from '../../theme/colors';

export default function TaskCard({task,onPress}:{task:FocusTask;onPress:()=>void}){
  const critical=task.priority==='critical';
  return <Pressable onPress={onPress} style={[styles.card,task.done&&styles.done,critical&&styles.critical]}>
    <View style={styles.check}><Text style={styles.checkText}>{task.done?'✓':''}</Text></View>
    <View style={{flex:1}}>
      <Text style={[styles.title,task.done&&styles.doneText]}>{task.title}</Text>
      <View style={styles.meta}><Text style={styles.pill}>{task.type}</Text><Text style={[styles.pill,critical&&styles.criticalPill]}>{critical?'Kritik':task.priority==='light'?'Hafif':'Normal'}</Text></View>
    </View>
  </Pressable>;
}
const styles=StyleSheet.create({
  card:{flexDirection:'row',gap:12,padding:15,borderRadius:22,backgroundColor:'rgba(15,23,42,.72)',borderWidth:1,borderColor:'rgba(255,255,255,.1)',marginBottom:10},
  done:{backgroundColor:'rgba(34,197,94,.08)',borderColor:'rgba(34,197,94,.22)'}, critical:{borderColor:'rgba(239,68,68,.30)'},
  check:{width:28,height:28,borderRadius:10,borderWidth:1,borderColor:'rgba(255,255,255,.18)',alignItems:'center',justifyContent:'center'},
  checkText:{color:COLORS.green,fontWeight:'900'}, title:{color:COLORS.text,fontWeight:'900',fontSize:15}, doneText:{color:COLORS.muted,textDecorationLine:'line-through'},
  meta:{flexDirection:'row',gap:6,marginTop:8}, pill:{color:'#BAE6FD',fontSize:11,fontWeight:'900',paddingHorizontal:8,paddingVertical:4,borderRadius:99,backgroundColor:'rgba(56,189,248,.10)'},
  criticalPill:{color:'#FECACA',backgroundColor:'rgba(239,68,68,.12)'}
});
