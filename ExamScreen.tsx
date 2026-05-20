import React from 'react';
import { Pressable, StyleSheet, Text, ViewStyle } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { COLORS } from '../../theme/colors';

export default function NeonButton({title,onPress,variant='primary',style}:{title:string;onPress?:()=>void;variant?:'primary'|'dark'|'danger';style?:ViewStyle}) {
  const colors = variant==='primary' ? [COLORS.green,'#16A34A'] : variant==='danger' ? [COLORS.orange,'#EA580C'] : ['rgba(15,23,42,.95)','rgba(2,6,23,.92)'];
  return <Pressable onPress={onPress} style={({pressed})=>[styles.press,pressed&&styles.pressed,style]}>
    <LinearGradient colors={colors} style={styles.btn}><Text style={styles.text}>{title}</Text></LinearGradient>
  </Pressable>;
}
const styles=StyleSheet.create({
  press:{borderRadius:20,overflow:'hidden'}, pressed:{transform:[{scale:.98}]},
  btn:{minHeight:54,borderRadius:20,alignItems:'center',justifyContent:'center',paddingHorizontal:16,borderWidth:1,borderColor:'rgba(255,255,255,.12)'},
  text:{color:COLORS.text,fontWeight:'900',fontSize:15}
});
