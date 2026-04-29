from django.shortcuts import render, get_object_or_404
from .models import Article
from django.contrib.auth.models import User

def article_list(request):
    articles = Article.objects.all()
    return render(request, 'news/article_list.html', {'articles': articles})

def article_detail(request, pk):
    article = get_object_or_404(Article, pk=pk)
    return render(request, 'news/article_detail.html', {'article': article})

def author_articles(request, username):
    author = get_object_or_404(User, username=username)

    articles = Article.objects.filter(author=author) 
    return render(request, 'news/author_articles.html', {'author': author, 'articles': articles})

from rest_framework import generics
from .serializers import ArticleSerializer

class ArticleAPIView(generics.ListAPIView):
    queryset = Article.objects.all()
    serializer_class = ArticleSerializer