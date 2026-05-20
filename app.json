import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { COLORS } from '../../theme/colors';
const fmt=(sec:number)=>`${String(Math.floor(sec/60)).padStart(2,'0')}:${String(sec%60).padStart(2,'0')}`;
export default function TimerRing({remaining}:{remaining:number}){
  return <View style={styles.ring}><Text style={styles.time}>{fmt(remaining)}</Text><Text style={styles.label}>Odak Modu</Text></View>;
}
const styles=StyleSheet.create({
  ring:{width:260,height:260,borderRadius:130,alignItems:'center',justifyContent:'center',alignSelf:'center',backgroundColor:'rgba(15,23,42,.72)',borderWidth:2,borderColor:'rgba(250,204,21,.42)',shadowColor:COLORS.primary,shadowOpacity:.25,shadowRadius:34,elevation:18},
  time:{color:COLORS.text,fontSize:58,fontWeight:'900',letterSpacing:-2}, label:{color:COLORS.primary,fontWeight:'900',marginTop:8}
});
