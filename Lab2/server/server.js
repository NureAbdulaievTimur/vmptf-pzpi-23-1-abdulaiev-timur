const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

let articles = [
  { id: 1, title: 'Вступ до Node.js', content: 'Node.js це круто!', category: 'tech', comments: [] },
  { id: 2, title: 'Що нового в React', content: 'Хуки змінили все.', category: 'frontend', comments: [] }
];

let articleCache = {
  data: null,
  isValid: false
};

const checkAuth = (req, res, next) => {
  const token = req.headers['authorization'];
  if (token === 'Bearer super-secret-token') {
    next();
  } else {
    res.status(401).json({ message: 'Не авторизовано' });
  }
};

app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  if (username === 'admin' && password === '1234') {
    res.json({ token: 'super-secret-token' });
  } else {
    res.status(401).json({ message: 'Невірні дані' });
  }
});

app.get('/api/articles', (req, res) => {
  const { search, category } = req.query;

  if (!search && !category && articleCache.isValid) {
    console.log("Віддаємо дані з кешу!");
    return res.json(articleCache.data);
  }

  console.log("Обробляємо запит (без кешу)...");
  let result = articles;

  if (category) {
    result = result.filter(a => a.category === category);
  }
  if (search) {
    result = result.filter(a => a.title.toLowerCase().includes(search.toLowerCase()));
  }

  if (!search && !category) {
    articleCache.data = result;
    articleCache.isValid = true;
  }

  res.json(result);
});

app.post('/api/articles', checkAuth, (req, res) => {
  const { title, content, category } = req.body;
  const newArticle = {
    id: Date.now(),
    title,
    content,
    category: category || 'general',
    comments: []
  };
  articles.push(newArticle);
  
  articleCache.isValid = false; 

  res.status(201).json(newArticle);
});

app.post('/api/articles/:id/comments', (req, res) => {
  const articleId = parseInt(req.params.id);
  const { text } = req.body;
  
  const article = articles.find(a => a.id === articleId);
  if (article) {
    article.comments.push({ id: Date.now(), text });
    articleCache.isValid = false;
    res.status(201).json({ message: 'Коментар додано' });
  } else {
    res.status(404).json({ message: 'Статтю не знайдено' });
  }
});

app.listen(5000, () => {
  console.log('Сервер працює на http://localhost:5000');
});