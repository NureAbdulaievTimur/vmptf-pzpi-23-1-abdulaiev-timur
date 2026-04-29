import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [articles, setArticles] = useState([]);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  
  const [token, setToken] = useState(null);
  const [loginData, setLoginData] = useState({ username: '', password: '' });
  
  const [newArticle, setNewArticle] = useState({ title: '', content: '', category: 'tech' });
  const [commentText, setCommentText] = useState({});

  const fetchArticles = () => {
    let url = 'http://localhost:5000/api/articles?';
    if (search) url += `search=${search}&`;
    if (categoryFilter) url += `category=${categoryFilter}`;

    fetch(url)
      .then(res => res.json())
      .then(data => setArticles(data))
      .catch(err => console.error("Помилка:", err));
  };

  useEffect(() => {
    fetchArticles();
  }, [search, categoryFilter]);

  // Логін
  const handleLogin = (e) => {
    e.preventDefault();
    fetch('http://localhost:5000/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginData)
    })
    .then(res => res.json())
    .then(data => {
      if (data.token) {
        setToken(data.token);
        alert('Ви успішно увійшли!');
      } else {
        alert('Невірний логін або пароль (Використовуйте admin/1234)');
      }
    });
  };

  const handleAddArticle = (e) => {
    e.preventDefault();
    fetch('http://localhost:5000/api/articles', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      },
      body: JSON.stringify(newArticle)
    }).then(() => {
      fetchArticles();
      setNewArticle({ title: '', content: '', category: 'tech' });
    });
  };

  const handleAddComment = (articleId) => {
    fetch(`http://localhost:5000/api/articles/${articleId}/comments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: commentText[articleId] })
    }).then(() => {
      fetchArticles();
      setCommentText({ ...commentText, [articleId]: '' });
    });
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>Блог на Node.js + React</h1>

      {!token ? (
        <form onSubmit={handleLogin} style={{ marginBottom: '20px', padding: '10px', background: '#f0f0f0' }}>
          <h3>Вхід для авторів</h3>
          <input placeholder="Логін" onChange={e => setLoginData({...loginData, username: e.target.value})} />
          <input type="password" placeholder="Пароль" onChange={e => setLoginData({...loginData, password: e.target.value})} />
          <button type="submit">Увійти</button>
          <small> (admin / 1234)</small>
        </form>
      ) : (
        <div style={{ marginBottom: '20px', padding: '10px', background: '#e0ffe0' }}>
          <h3>Додати нову статтю (Рівень 2)</h3>
          <form onSubmit={handleAddArticle}>
            <input required placeholder="Заголовок" value={newArticle.title} onChange={e => setNewArticle({...newArticle, title: e.target.value})} />
            <input required placeholder="Текст" value={newArticle.content} onChange={e => setNewArticle({...newArticle, content: e.target.value})} />
            <select value={newArticle.category} onChange={e => setNewArticle({...newArticle, category: e.target.value})}>
              <option value="tech">Технології</option>
              <option value="frontend">Фронтенд</option>
              <option value="general">Загальне</option>
            </select>
            <button type="submit">Створити</button>
          </form>
        </div>
      )}

      <div style={{ marginBottom: '20px' }}>
        <input placeholder="Пошук за заголовком..." value={search} onChange={e => setSearch(e.target.value)} />
        <select value={categoryFilter} onChange={e => setCategoryFilter(e.target.value)} style={{ marginLeft: '10px' }}>
          <option value="">Всі категорії</option>
          <option value="tech">Технології</option>
          <option value="frontend">Фронтенд</option>
          <option value="general">Загальне</option>
        </select>
      </div>

      <div>
        {articles.map(article => (
          <div key={article.id} style={{ border: '1px solid #ccc', padding: '15px', marginBottom: '15px' }}>
            <h2>{article.title} <small style={{fontSize: '12px', color: 'gray'}}>({article.category})</small></h2>
            <p>{article.content}</p>
            
            <hr />
            <h4>Коментарі:</h4>
            <ul>
              {article.comments.map(c => <li key={c.id}>{c.text}</li>)}
            </ul>
            <div>
              <input 
                placeholder="Ваш коментар..." 
                value={commentText[article.id] || ''} 
                onChange={e => setCommentText({ ...commentText, [article.id]: e.target.value })}
              />
              <button onClick={() => handleAddComment(article.id)}>Надіслати</button>
            </div>
          </div>
        ))}
        {articles.length === 0 && <p>Статей не знайдено.</p>}
      </div>
    </div>
  );
}

export default App;