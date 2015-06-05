using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class AndroidEventPropagator : IEventsQueue
    {
        private HttpClient _httpClient;

        public bool IsWorking { get; private set; }

        private const int DEFAULT_WAITING_TIME = 500;
        private const int MAX_WAITING_TIME = 5000;
        private const int ATTEMPTS = 12;

        private readonly static AndroidEventPropagator _instance = new AndroidEventPropagator();

        private static ConcurrentQueue<IEvent> _eventsQueue;

        public static AndroidEventPropagator Instance {
            get
            {
                return _instance;
            }
        }

        private AndroidEventPropagator()
        {
            if (_eventsQueue == null) _eventsQueue = new ConcurrentQueue<IEvent>();
            _httpClient = new HttpClient();
        }

        void IEventsQueue.Enqueue(IEvent command)
        {
            _eventsQueue.Enqueue(command);
        }

        public void StartWorking()
        {
            if (!IsWorking)
            {
                Log("~ starting perfomer");
                IsWorking = true;
                new Thread(Run).Start();
            }
        }

        private void Run()
        {
            Log("~ performer starts running");
            int waitingTime = DEFAULT_WAITING_TIME;
            int currentAttempt = -1;
            while (IsWorking)
            {
                IEvent ievent;
                _eventsQueue.TryDequeue(out ievent);
                if (ievent != null)
                {
                    currentAttempt = 0;
                    waitingTime = DEFAULT_WAITING_TIME;

                    try
                    {
                        if (ievent is LocationCreated)
                        {
                            Handle(ievent as LocationCreated);
                        }
                        else if (ievent is LocationUpdated)
                        {
                            Handle(ievent as LocationUpdated);
                        }
                        else if (ievent is LocationDeleted)
                        {
                            Handle(ievent as LocationDeleted);
                        }
                        else if (ievent is OfferingCreated)
                        {
                            Handle(ievent as OfferingCreated);
                        }
                        else if (ievent is OfferingUpdated)
                        {
                            Handle(ievent as OfferingUpdated);
                        }
                        else if (ievent is OfferingDeleted)
                        {
                            Handle(ievent as OfferingDeleted);
                        }
                    }
                    catch (Exception ex)
                    {
                        Log(String.Format("Exception occurred:\n{0}\n", ex.Message), ConsoleColor.Red);
                    }
                }
                else
                {
                    currentAttempt++;
                }

                if (currentAttempt >= ATTEMPTS)
                {
                    currentAttempt = 0;
                    if (waitingTime < MAX_WAITING_TIME)
                        waitingTime += 250;
                    Log("~ Increase waiting time to " + waitingTime, ConsoleColor.DarkYellow);
                }

                Thread.Sleep(waitingTime);
            }

            Log("~ performer stopped");
        }

        public void StopWorking()
        {
            Log("~ stopping perfomer");
            IsWorking = false;
        }

        private void Log(string message)
        {
            Log(message, ConsoleColor.DarkGray);
        }

        private void Log(string message, ConsoleColor color)
        {
            ConsoleColor oldColor = Console.ForegroundColor;
            Console.ForegroundColor = color;
            Console.WriteLine(message);
            Console.ForegroundColor = oldColor;
        }

        private void Handle(LocationCreated ievent)
        {
            NotifyAffected(ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationUpdated ievent)
        {
            NotifyAffected(ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationDeleted ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingCreated ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingUpdated ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingDeleted ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void NotifyAffected(string lat, string lng)
        {
            IEnumerable<string> keys = PositionsContainer.Instance.GetKeysAffected(Double.Parse(lat), Double.Parse(lng));
            //push
        }

        private void Push(IEnumerable<string> keys)
        {

        }
    }
}
