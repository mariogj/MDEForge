package org.mdeforge.business;

import java.util.List;

import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.SimilarityRelation;

public interface SimilarityRelationalService extends CRUDRelationService<SimilarityRelation>{
	List<SimilarityRelation> findAll(int computation) throws BusinessException;
	List<SimilarityRelation> findAll(double threshold) throws BusinessException;
	List<SimilarityRelation> findAll(double threshold, int computation) throws BusinessException;

	List<SimilarityRelation> findByEcoreMetamodel(EcoreMetamodel emm, double threshold) throws BusinessException;
	List<SimilarityRelation> findByEcoreMetamodel(EcoreMetamodel emm, double threshold, int computation);
}
