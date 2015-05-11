using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestOData.Models
{
    public class Repository
    {
        private IList<Product> _products;

        private static readonly Repository _singleton = new Repository();

        public static Repository Instance {
            get
            {
                return _singleton;
            }
        }

        private Repository()
        {
            CreateProducts();
        }

        public IEnumerable<Product> GetProducts()
        {
            return _products;
        }

        private void CreateProducts()
        {
            Random rand = new Random(4);
            _products = new List<Product>();
            for (int i = 0; i < 50; i++)
            {
                _products.Add(new Product
                {
                    Id = i,
                    Name = String.Format("{0}{1}{2}", (char)(rand.Next(26) + 65),
                                                      (char)(rand.Next(26) + 97),
                                                      (char)(rand.Next(26) + 97)),
                    CategoryId = i % 4
                });
            }
        }
    }
}
