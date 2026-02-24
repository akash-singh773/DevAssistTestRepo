using System;
using System.IO;
using System.Data.SqlClient;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Security.Cryptography;
using System.Xml;
using System.Reflection;
using System.Runtime.Serialization.Formatters.Binary;
using System.DirectoryServices;
using System.Threading.Tasks;

namespace VulnerableTestApp
{
    public class VulnerableExamples
    {
        // Magic Number
        private const int MAGIC = 1337;

        public void UnsafeSql(string userInput)
        {
            // Unsafe SQL Query Construction
            string query = "SELECT * FROM Users WHERE Username = '" + userInput + "'";
            SqlConnection conn = new SqlConnection("Server=.;Database=TestDB;Trusted_Connection=True;");
            SqlCommand cmd = new SqlCommand(query, conn);
            conn.Open();
            cmd.ExecuteReader(); // Potential resource leak (not closed)
        }

        public void InsecureFileCreation()
        {
            // Hardcoded File Path + Improper File Permissions
            string path = "C:\\temp\\test.txt";
            FileStream fs = File.Create(path); // No permissions set
            StreamWriter sw = new StreamWriter(fs); // No encoding set
            sw.WriteLine("Sensitive Data");
            // Resource leak - not closing writer or stream
        }

        public void InsecureLogging(string password)
        {
            // Insecure Logging of Sensitive Information
            Console.WriteLine("User password is: " + password);
        }

        public void WeakCrypto()
        {
            // Deprecated Cryptographic Algorithm (MD5)
            MD5 md5 = MD5.Create();
            byte[] hash = md5.ComputeHash(Encoding.UTF8.GetBytes("password"));

            // Weak Encryption Mode + Non-random IV
            Aes aes = Aes.Create();
            aes.Mode = CipherMode.ECB; // Weak mode
            aes.IV = new byte[16]; // Non-random IV
        }

        public async Task UnsafeRestCall()
        {
            // REST Call without timeout + missing error handling
            HttpClient client = new HttpClient();
            var response = await client.GetAsync("https://example.com/api");
            string content = await response.Content.ReadAsStringAsync();

            // Missing HTTP status code validation
            Console.WriteLine(content);
        }

        public void UnsafeCookie(HttpResponse response)
        {
            // Cookie without HttpOnly and without Domain
            HttpCookie cookie = new HttpCookie("SessionId", "12345");
            response.Cookies.Add(cookie);
        }

        public void PotentialXSS(string input)
        {
            // Potential XSS
            Console.WriteLine("<div>" + input + "</div>");
        }

        public void UnsafeDeserialization()
        {
            // Unsafe Deserialization
            BinaryFormatter formatter = new BinaryFormatter();
            using (FileStream fs = new FileStream("data.bin", FileMode.Open))
            {
                var obj = formatter.Deserialize(fs);
            }
        }

        public void UnsafePathHandling(string filename)
        {
            // Unsafe Path Handling
            string path = "C:\\uploads\\" + filename;
            File.ReadAllText(path);
        }

        public void DeleteFile()
        {
            // File Deletion without checking existence
            File.Delete("C:\\temp\\old.txt");
        }

        public void InsecureExceptionHandling()
        {
            try
            {
                int x = 10 / 0;
            }
            catch (Exception ex)
            {
                // Revealing stack trace
                Console.WriteLine(ex.ToString());
            }
        }

        public void UnsafeCodeExecution(string userInput)
        {
            // Unsafe Code Execution
            System.Diagnostics.Process.Start("cmd.exe", "/c " + userInput);
        }

        public void UnsafeXPath(string userInput)
        {
            // Unsafe XPath String
            XmlDocument doc = new XmlDocument();
            doc.LoadXml("<users><user name='admin'/></users>");
            XmlNode node = doc.SelectSingleNode("//user[@name='" + userInput + "']");
        }

        public void UnsafeReflection(string className)
        {
            // Unsafe Reflection
            Type type = Type.GetType(className);
            Activator.CreateInstance(type);
        }

        public void UnsafeLdapSearch(string userInput)
        {
            // Unsafe LDAP Search
            DirectorySearcher searcher = new DirectorySearcher();
            searcher.Filter = "(cn=" + userInput + ")";
            searcher.FindOne();
        }

        public void ConnectionWithoutTimeout()
        {
            // Connection without timeout
            SqlConnection conn = new SqlConnection("Server=.;Database=TestDB;Trusted_Connection=True;");
            conn.Open();
        }

        public void FileCreationWithoutPermissions()
        {
            // File Creation without setting permissions
            File.WriteAllText("C:\\temp\\public.txt", "data");
        }
    }
}
