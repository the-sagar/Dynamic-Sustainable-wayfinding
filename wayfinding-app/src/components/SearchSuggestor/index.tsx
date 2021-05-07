import React, {useState} from 'react';
import { View, StyleSheet, FlatList, NativeSyntheticEvent, TextInputFocusEventData } from 'react-native';
import { List, Searchbar, useTheme } from 'react-native-paper';
import tailwind from 'tailwind-rn';
import Highlighter from './Highlighter';

type Props = {
  onIconPress?: (search: string) => void,
  fetchSuggestions?: (search: string) => void,
  suggestions?: Array<Item>,
  onFocus?: (e: NativeSyntheticEvent<TextInputFocusEventData>) => void,
  onSelect?: (select: string) => void,
};

export interface Item {
  id: string;
  name: string;
  details: string;
  avatar?: string;
}

function Item({ item, search, onSelect }: { item: Item, search: string, onSelect?: (select: string)=>void}) {
  const theme = useTheme();
  const styles = StyleSheet.create({
    listItemContainer: {
      ...tailwind("w-full"),
      backgroundColor: theme.colors.surface,
    },
    listItemTitleHighlight: {
      ...tailwind("font-bold bg-yellow-200"),
      color: theme.colors.text,
    },
    listItemTitle: {
      color: theme.colors.text,
    },
  });

  return (
    <List.Item
      left={props => item.avatar? <List.Icon {...props} icon={item.avatar}/>: undefined}
      title={//@ts-ignore
        <Highlighter
          highlightStyle={styles.listItemTitleHighlight}
          searchWords={search.split(" ")}
          textToHighlight={item.name}
          style={styles.listItemTitle}
        />}
      description={item.details}
      style={styles.listItemContainer}
      onPress={()=>{
        if(onSelect)
          onSelect(item.id);
      }}
    />
  );
}

const SearchSuggestor: React.FC<Props> = ({onIconPress, onFocus, fetchSuggestions, suggestions, onSelect}) => {
  // const theme = useTheme();
  const styles = StyleSheet.create({
    container: tailwind("flex"),
    itemView: tailwind("py-5 w-full border-gray-500"),
  });
  const [searchQuery, setSearchQuery] = useState('');

  const handleInput = (query: string) => {
    setSearchQuery(query);
    if(fetchSuggestions) {
      if(query.length > 0)
        fetchSuggestions(query);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.container}>
        {/*
        // @ts-ignore */}
        <Searchbar
          autoFocus
          placeholder="Search"
          onChangeText={handleInput}
          value={searchQuery}
          onFocus={onFocus}
          onIconPress={()=>{
            if(onIconPress)
              onIconPress(searchQuery);
          }}
        />
        <FlatList
          data={suggestions}
          renderItem={({ item }: { item: Item }) => <Item item={item} search={searchQuery} onSelect={onSelect}/>}
          keyExtractor={(item: Item) => item.id}
        />
      </View>
    </View>
  );
};

export default SearchSuggestor;
