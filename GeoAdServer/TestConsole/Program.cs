using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestConsole
{
    class Program
    {
        static void Main(string[] args)
        {
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            ICategoriesRepository pgrsRep = new PostgresqlCategoriesRepository(connectionString);
            foreach (string cat in pgrsRep.GetAll())
                Console.WriteLine(cat);
        }
    }
}
