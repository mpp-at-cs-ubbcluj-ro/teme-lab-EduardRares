import React, {useState} from 'react';

export default function FlightForm({addFunc}){

    const [flightId, setFlightId] = useState('');
    const [airport, setAirport] = useState('');
    const [destination, setDestination] = useState('');
    const [numberOfAvailableSeats, setNumberOfAvailableSeats] = useState('');
    const [departureTimeMillis, setDepartureTimeMillis] = useState('');
    const [departureTime, setDepartureTime] = useState('');


   function handleSubmit (event){
       const millis = new Date(departureTimeMillis).getTime();
        let flight={id:flightId,
            airport: airport,
            destination: destination,
            numberOfAvailableSeats: numberOfAvailableSeats,
            departureTime: departureTime,
            departureTimeMillis: millis
        }
        console.log('A flight was submitted: ');
        console.log(flight);
         addFunc(flight);
        event.preventDefault();
    }
    return(
    <form onSubmit={handleSubmit}>
        <label>
            Flight id:
            <input type="text" value={flightId}  onChange={e=>setFlightId(e.target.value)}  />
        </label><br/>
        <label>
            Airport:
            <input type="text" value={airport} onChange={e=>setAirport(e.target.value)} />
        </label><br/>
        <label>
            Destination:
            <input type="text" value={destination} onChange={e=>setDestination(e.target.value)} />
        </label><br/>
        <label>
            Number of available seats:
            <input type="number" value={numberOfAvailableSeats}  onChange={e=>setNumberOfAvailableSeats(e.target.value)}  />
        </label><br/>
        <label>
            Departure Date:
            <input type="date" value={departureTimeMillis}  onChange={e=>setDepartureTimeMillis(e.target.value)}  />
        </label><br/>
        <label>
            Number of available seats:
            <input type="time" value={departureTime}  onChange={e=>setDepartureTime(e.target.value)}  />
        </label><br/>


        <input type="submit" value="Add flight" />
    </form>);
}
