from django.urls import path
from . import views

urlpatterns = [
    path('', views.article_list, name='article_list'),
    path('article/<int:pk>/', views.article_detail, name='article_detail'),
    path('author/<str:username>/', views.author_articles, name='author_articles'),
    path('api/articles/', views.ArticleAPIView.as_view(), name='api_articles'),
]