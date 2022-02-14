import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { Button} from 'react-native';

var status = false

function hideandshow(){
  if (status == true){
    console.log("heyo")
    status = false
  }else{
    console.log("prout")
    status = true
  }
}



function wait(timeout) {
  return new Promise(resolve => setTimeout(resolve, timeout));
}

export default function App() {

  const onRefresh = React.useCallback(() => {
    setRefreshing(true);
    wait(500).then(() => setRefreshing(false));
  }, []);

  const [refreshing, setRefreshing] = React.useState(false);

  return (
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>
      {
        status ? <Text>This is a second</Text> : null
      }
      <Button title="reload!" onPress={onRefresh}></Button>
      <Button title="Hide&Show!" onPress={hideandshow}></Button>
      <StatusBar style="auto" />
    </View>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
