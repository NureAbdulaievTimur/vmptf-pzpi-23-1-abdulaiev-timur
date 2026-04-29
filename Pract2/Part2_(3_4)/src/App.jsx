import React, { useState, useEffect } from 'react';

function App() {

  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetch('https://jsonplaceholder.typicode.com/users')
      .then(response => response.json())
      .then(data => {
        const usersWithGroups = data.map(user => ({
          ...user,
          group: 'Інші'
        }));
        setUsers(usersWithGroups);
      })
      .catch(error => console.log("Помилка API:", error));
  }, []);

  const handleGroupChange = (userId, newGroup) => {
    const updatedUsers = users.map(user => 
      user.id === userId ? { ...user, group: newGroup } : user
    );
    setUsers(updatedUsers);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial', lineHeight: 1.3 }}>
      <h1>Список користувачів (Дані з JSONPlaceholder)</h1>
      
      {users.map(user => (
        <div key={user.id} style={{ border: '1px solid #ccc', margin: '10px 0', padding: '10px' }}>
          <h3>{user.name}</h3>
          <p>Email: {user.email}</p>
          
          <label>Група клієнта: </label>
          <select 
            value={user.group} 
            onChange={(e) => handleGroupChange(user.id, e.target.value)}
          >
            <option value="Інші">Інші</option>
            <option value="Цільова аудиторія (ЦА)">Цільова аудиторія (ЦА)</option>
            <option value="Постійні клієнти">Постійні клієнти</option>
          </select>
        </div>
      ))}
    </div>
  );
}

export default App;