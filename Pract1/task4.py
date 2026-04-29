print("\nРівень 4 (варіант 1)\n")

class Book:

    def __init__(self, title, author, year):
        self.title = title
        self.author = author
        self.year = year

    def display_info(self):
        print(f"Книга: '{self.title}'")
        print(f"Автор: {self.author}")
        print(f"Рік видання: {self.year}")

book_test = Book(title="Пістрява стрічка", author="Артур Конан Дойл", year=1892)

book_test.display_info()