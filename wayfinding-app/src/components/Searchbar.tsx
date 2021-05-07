import * as React from 'react';
import { Searchbar } from 'react-native-paper';

type Props = {
  onIconPress?: (search: string) => void,
};

const SearchBar: React.FC<Props> = ({onIconPress}) => {
  const [searchQuery, setSearchQuery] = React.useState('');

  const onChangeSearch = (query: React.SetStateAction<string>) => setSearchQuery(query);

  return (
    <Searchbar
      placeholder="Search location"
      onChangeText={onChangeSearch}
      value={searchQuery}
      onIconPress={()=>{
        if(onIconPress)
          onIconPress(searchQuery);
      }}
    />
  );
};

export default SearchBar;
