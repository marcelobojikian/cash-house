package br.com.housecash.backend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.com.housecash.backend.model.Cashier;
import br.com.housecash.backend.model.Dashboard;
import br.com.housecash.backend.model.Flatmate;
import br.com.housecash.backend.model.Transaction;
import br.com.housecash.backend.repository.custom.TransactionRepositoryFilter;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, TransactionRepositoryFilter {
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions}")
	public Collection<Transaction> findByDashboard(@Param("dashboard") Dashboard dashboard);
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions} AND t.cashier = :cashier")
	public Collection<Transaction> findByDashboardAndCashier(@Param("dashboard") Dashboard dashboard, @Param("cashier") Cashier cashier);
	
	@Query("SELECT t FROM Transaction t WHERE t IN :#{#dashboard.transactions}")
	public Page<Transaction> findByDashboard(@Param("dashboard") Dashboard dashboard, Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE t.id = :id AND t IN :#{#dashboard.transactions}")
	public Optional<Transaction> findByDashboardAndId(@Param("dashboard") Dashboard dashboard, @Param("id") long id);

	@Query("SELECT t FROM Dashboard d JOIN d.transactions t WHERE d.owner = :flatmateLogged")
	public List<Object[]> findByUserLogged(@Param("flatmateLogged") Flatmate flatmate);

//	@Query(value = "SELECT "
//	        + " new br.com.cueva.webservice.model.dto.TransactionGroupByDate(t.createdDate, EXTRACT (year FROM t.createdDate), t)"
////	        + "																 DATE_FORMAT(operation.production.productionDate,'%Y-%m'), "
////	        + "																 DATE_FORMAT(operation.production.productionDate,'%Y-%m')"
////	        + "	SUM(operation.actualQuantity), SUM(operation.plannedQuantity), (SUM(operation.actualQuantity)/SUM(operation.plannedQuantity))*100) "
//	        + " FROM Transaction t" 
////	        EXTRACT (day FROM b.startDate)
////	        + " WHERE operation.production.productionDate BETWEEN :startDate AND :endDate"
////	        + " AND operation.production.controlPoint.workCenter.costCenter.section= :section"
//	        + " GROUP BY t.id, t.createdDate")
//	public List getTransactionGroupDate();
	
	@Query(value = "SELECT EXTRACT(year FROM t.createdDate), EXTRACT(month FROM t.createdDate), EXTRACT(day FROM t.createdDate), t"
			+ " FROM Transaction t GROUP BY EXTRACT(year FROM t.createdDate), EXTRACT(month FROM t.createdDate), EXTRACT(day FROM t.createdDate), t.id ORDER BY t.createdDate")
	public List<Object[]> getTransactionGroupDate20();
	
	@Query(value = "SELECT DISTINCT EXTRACT(day FROM t.createdDate), t"
					+ " FROM Dashboard d JOIN d.transactions t"
					+ " GROUP BY EXTRACT(day FROM t.createdDate), t.id")
	public List<Object[]> getTransactionGroupDate2();

//	@Query(value = "SELECT "
//	        + " new br.com.cueva.webservice.model.dto.TransactionGroupByDate(t.createdDate, EXTRACT (year FROM t.createdDate), EXTRACT (month FROM t.createdDate), EXTRACT (day FROM t.createdDate), t)"
//	        + " FROM Transaction t")
//	public List getTransactionGroupDate3();
	
//	@Query(value = "SELECT "
//	        + " new br.com.cueva.webservice.model.dto.TransactionGroupByDate(DATE_FORMAT(operation.production.productionDate,'%Y-%m'), SUM(operation.actualQuantity), SUM(operation.plannedQuantity), (SUM(operation.actualQuantity)/SUM(operation.plannedQuantity))*100) "
//	        + " FROM Operation operation" 
//	        + " WHERE operation.production.productionDate BETWEEN :startDate AND :endDate"
//	        + " AND operation.production.controlPoint.workCenter.costCenter.section= :section"
//	        + " GROUP BY DATE_FORMAT(operation.production.productionDate,'%Y-%m')")
//	public List getMonthlyScheduleAdherenceBySection(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("section") Section section);
	
}
