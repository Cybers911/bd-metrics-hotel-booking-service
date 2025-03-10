package com.amazon.ata.metrics.classroom.activity;

import com.amazon.ata.metrics.classroom.dao.ReservationDao;
import com.amazon.ata.metrics.classroom.dao.models.UpdatedReservation;
import com.amazon.ata.metrics.classroom.metrics.MetricsConstants;
import com.amazon.ata.metrics.classroom.metrics.MetricsPublisher;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.time.ZonedDateTime;
import javax.inject.Inject;

/**
 * Handles requests to modify a reservation
 */
public class ModifyReservationActivity {

    private ReservationDao reservationDao;
    private MetricsPublisher metricsPublisher;

    /**
     * Construct ModifyReservationActivity.
     * @param reservationDao Dao used for modify reservations.
     */
    @Inject
    public ModifyReservationActivity(ReservationDao reservationDao, MetricsPublisher metricsPublisher) {
        this.reservationDao = reservationDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Modifies the given reservation.
     * @param reservationId Id to modify reservations for
     * @param checkInDate modified check in date
     * @param numberOfNights modified number of nights
     * @return UpdatedReservation that includes the old reservation and the updated reservation details.
     */
    public UpdatedReservation handleRequest(final String reservationId, final ZonedDateTime checkInDate,
                                            final Integer numberOfNights) {

        UpdatedReservation updatedReservation = reservationDao.modifyReservation(reservationId, checkInDate,
            numberOfNights);

        //Modify the reservation and UPDATE IT
        //And update the ReservationRevenueCount metric with the total cost of the reservation.

        //Update the metrics publisher to publish the metric Update the ModifyReservationCount metric

                                    //classofEnum.enumName
        metricsPublisher.addMetric(MetricsConstants.MODIFY_COUNT, 1, StandardUnit.Count);

        //Update the ReservationRevenue Count metric with the total cost of the reservation.
        //The Reservation is stored in response upon returning from the handleRequest
        // method ReservationDAO.bookReservation(reservation)
        //It contain the original reservation and the modified reservation
        //If we substract the totalCost from the original reservation from the modified reservation
        // we get the total cost of the reservation then we use it to update the ReservationRevenueCount metric.
        //

        //Calculate the difference due the modification

                double revenueDifference = updatedReservation.getModifiedReservation()
         .getTotalCost().subtract(updatedReservation.getOriginalReservation()
         .getTotalCost()).doubleValue();

        //Add the difference to the ReservationRevenueCount metric
        //Update the ReservationRevenueCount metric with the total cost of the reservation.

        metricsPublisher.addMetric(MetricsConstants.RESERVATION_REVENUE
                , revenueDifference
                , StandardUnit.None);


        return updatedReservation;
    }
}
