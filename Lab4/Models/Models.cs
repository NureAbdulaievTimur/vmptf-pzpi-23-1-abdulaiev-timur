using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Lab4_ECommerce.Models
{
    public class User
    {
        public int Id { get; set; }
        public string Username { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        [JsonIgnore] public string PasswordHash { get; set; } = string.Empty;
        public string Role { get; set; } = "User";

        [JsonIgnore] public ICollection<Order> Orders { get; set; } = new List<Order>();
        [JsonIgnore] public ICollection<Review> Reviews { get; set; } = new List<Review>();
    }

    public class Category
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }

        [JsonIgnore] public ICollection<ProductCategory> ProductCategories { get; set; } = new List<ProductCategory>();
    }

    public class Product
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public decimal Price { get; set; }
        public int StockQuantity { get; set; }

        [JsonIgnore] public ICollection<ProductCategory> ProductCategories { get; set; } = new List<ProductCategory>();
        [JsonIgnore] public ICollection<Review> Reviews { get; set; } = new List<Review>();
    }

    public class ProductCategory
    {
        public int ProductId { get; set; }
        public Product Product { get; set; } = null!;
        public int CategoryId { get; set; }
        public Category Category { get; set; } = null!;
    }

    public class Order
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public User User { get; set; } = null!;
        public DateTime OrderDate { get; set; } = DateTime.UtcNow;
        public decimal TotalAmount { get; set; }

        public ICollection<OrderItem> OrderItems { get; set; } = new List<OrderItem>();
    }

    public class OrderItem
    {
        public int OrderId { get; set; }
        [JsonIgnore] public Order Order { get; set; } = null!;
        public int ProductId { get; set; }
        public Product Product { get; set; } = null!;
        public int Quantity { get; set; }
        public decimal UnitPrice { get; set; }
    }

    public class Review
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public User User { get; set; } = null!;
        public int ProductId { get; set; }
        [JsonIgnore] public Product Product { get; set; } = null!;
        [Range(1, 5)] public int Rating { get; set; }
        public string? Comment { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}