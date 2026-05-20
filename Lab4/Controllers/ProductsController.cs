using Lab4_ECommerce.Models;
using Lab4_ECommerce;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ECommerceAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductsController : ControllerBase
    {
        private readonly AppDbContext _context;
        public ProductsController(AppDbContext context) => _context = context;

        [AllowAnonymous]
        [HttpGet]
        public async Task<IActionResult> GetProducts() => Ok(await _context.Products.ToListAsync());

        [AllowAnonymous]
        [HttpGet("{id}")]
        public async Task<IActionResult> GetProduct(int id)
        {
            var product = await _context.Products.FindAsync(id);
            return product == null ? NotFound() : Ok(product);
        }

        [Authorize(Roles = "Admin")]
        [HttpPost]
        public async Task<IActionResult> CreateProduct(Product product)
        {
            _context.Products.Add(product);
            await _context.SaveChangesAsync();
            return Ok(product);
        }

        [Authorize(Roles = "Admin")]
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateProduct(int id, Product product)
        {
            if (id != product.Id) return BadRequest();
            _context.Entry(product).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [Authorize(Roles = "Admin")]
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProduct(int id)
        {
            var product = await _context.Products.FindAsync(id);
            if (product == null) return NotFound();
            _context.Products.Remove(product);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [Authorize(Roles = "Admin")]
        [HttpPost("{productId}/category/{categoryId}")]
        public async Task<IActionResult> AddProductToCategory(int productId, int categoryId)
        {
            var product = await _context.Products.FindAsync(productId);
            var category = await _context.Categories.FindAsync(categoryId);

            if (product == null || category == null)
                return NotFound("Товар або категорію не знайдено.");

            var exists = await _context.ProductCategories
                .AnyAsync(pc => pc.ProductId == productId && pc.CategoryId == categoryId);

            if (exists)
                return BadRequest("Цей товар вже знаходиться у цій категорії.");

            var productCategory = new ProductCategory
            {
                ProductId = productId,
                CategoryId = categoryId
            };

            _context.ProductCategories.Add(productCategory);
            await _context.SaveChangesAsync();

            return Ok("Товар успішно додано до категорії.");
        }
    }
}